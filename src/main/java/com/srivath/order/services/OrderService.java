package com.srivath.order.services;

import com.srivath.ecombasedomain.dtos.CartOrderDto;
import com.srivath.ecombasedomain.dtos.OrderDto;
import com.srivath.ecombasedomain.dtos.PaymentInstance;
import com.srivath.ecombasedomain.events.Event;
import com.srivath.ecombasedomain.events.OrderPlacedEvent;
import com.srivath.ecombasedomain.events.PaymentCompletedEvent;
import com.srivath.ecombasedomain.events.PlaceOrderEvent;
import com.srivath.order.dtos.OrderDTO;
import com.srivath.order.dtos.OrderStatusDTO;
import com.srivath.order.exceptions.OrderNotFoundException;
import com.srivath.order.exceptions.OrderStatusChangeException;
import com.srivath.order.models.*;
import com.srivath.order.repositories.OrderEventRepository;
import com.srivath.order.repositories.OrderRepository;
import com.srivath.order.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderEventRepository orderEventRepository;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    @Autowired
    private PaymentRepository paymentRepository;

    OrderService(OrderRepository orderRepository, OrderEventRepository orderEventRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    public Order getOrderById(Long orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with " + orderId +" not found"));
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}",groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Event event) throws OrderNotFoundException {
        if (event != null && event.getEventName().equals("PLACE_ORDER"))
        {
            PlaceOrderEvent placeOrderEvent = (PlaceOrderEvent) event;

            //Populate Order from PlaceOrderEvent
            CartOrderDto cartOrderDto = placeOrderEvent.getCartOrderDto();
            Order order = new Order();
            order.setCartId(cartOrderDto.getCartId());
            order.setOrderAmount(cartOrderDto.getTotalAmount());
            LocalDateTime orderDateTime = LocalDateTime.now();
            order.setOrderDate(orderDateTime.toLocalDate());
            order.setUserEmail(cartOrderDto.getUserEmail());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setUserName(cartOrderDto.getUserName());
            order.setUserPhone(cartOrderDto.getUserPhone());
            Order savedOrder = orderRepository.save(order); //to getOrder ID

            //Create OrderEvent for the new order
            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrder(savedOrder);
            orderEvent.setBeforeStatus(savedOrder.getOrderStatus());
            orderEvent.setAfterStatus(OrderStatus.CONFIRMED);
            orderEvent.setDateTime(orderDateTime);
            OrderEvent savedOrderEvent = orderEventRepository.save(orderEvent);

            //Update Order with the new status and event
            savedOrder.setOrderStatus(OrderStatus.CONFIRMED);
            savedOrder.getOrderEvents().add(savedOrderEvent);
            Order finalOrder =orderRepository.save(savedOrder);
            pushOrderPlacedEvent(finalOrder);

        }

        if (event != null && event.getEventName().equals("PAYMENT_COMPLETED"))
        {
            PaymentCompletedEvent paymentCompletedEvent = (PaymentCompletedEvent) event;

            Long orderId = paymentCompletedEvent.getPaymentDto().getOrderId();
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with " + orderId +" not found"));
            OrderStatus orderStatus = paymentCompletedEvent.getPaymentDto().getStatus().equals("COMPLETED") ? OrderStatus.PAID_FULL: OrderStatus.PAID_PART;
            OrderStatus previousStatus = order.getOrderStatus();
            order.setOrderStatus(orderStatus);
            Order savedOrder = orderRepository.save(order);


            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setBeforeStatus(previousStatus);
            orderEvent.setAfterStatus(orderStatus);
            orderEvent.setDateTime(LocalDateTime.now());
            orderEvent.setOrder(savedOrder);
            OrderEvent savedOrderEvent = orderEventRepository.save(orderEvent);
            savedOrder.getOrderEvents().add(savedOrderEvent);

            if (savedOrder.getPayments().isEmpty()) {
                for (PaymentInstance paymentInstance : paymentCompletedEvent.getPaymentDto().getPaymentInstances()) {
                    Payment payment = new Payment();
                    payment.setPaymentMethod(paymentInstance.getPaymentMethod());
                    payment.setAmount(paymentInstance.getAmount());
                    payment.setAdditionalInfo(paymentInstance.getAdditionalInfo());
                    payment.setPaymentStatus(paymentInstance.getPaymentStatus());
                    payment.setPaymentDate(paymentInstance.getPaymentDate());
                    payment.setOrder(savedOrder);
                    Payment savedPayment = paymentRepository.save(payment);

                    savedOrder.getPayments().add(savedPayment);

                }
            }
            else
            {
                int numberOfPayments = paymentCompletedEvent.getPaymentDto().getPaymentInstances().size();
                PaymentInstance paymentInstance = paymentCompletedEvent.getPaymentDto().getPaymentInstances().get(numberOfPayments-1);
                Payment payment = new Payment();
                payment.setOrder(savedOrder);
                payment.setPaymentMethod(paymentInstance.getPaymentMethod());
                payment.setAmount(paymentInstance.getAmount());
                payment.setAdditionalInfo(paymentInstance.getAdditionalInfo());
                payment.setPaymentStatus(paymentInstance.getPaymentStatus());
                payment.setPaymentDate(paymentInstance.getPaymentDate());
                Payment savedPayment = paymentRepository.save(payment);
                savedOrder.getPayments().add(savedPayment);
            }
            // Save the updated order


            //Order savedOrder =
            orderRepository.save(savedOrder);
            //orderEvent.setOrder(savedOrder);


        }
    }

    public void pushOrderPlacedEvent(Order order)
    {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setUserEmail(order.getUserEmail());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setCartId(order.getCartId());
        orderDto.setOrderAmount(order.getOrderAmount());
        orderDto.setUserName(order.getUserName());
        orderDto.setUserPhone(order.getUserPhone());

        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(orderDto);
        // Push the event to Kafka
        kafkaTemplate.send(topicName, orderPlacedEvent);
    }

    public List<Order> getOrderByUserEmail(String email) {
        return orderRepository.findByUserEmail(email);
    }

    public Order updateOrderStatus(Long orderId, String status) throws OrderNotFoundException, OrderStatusChangeException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with " + orderId +" not found"));
        if (order.getOrderStatus() == OrderStatus.valueOf(status) ) {
            throw new OrderStatusChangeException("Order status is already " + status);
        }
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(order);
        orderEvent.setBeforeStatus(order.getOrderStatus());
        orderEvent.setAfterStatus(OrderStatus.valueOf(status));
        orderEvent.setDateTime(LocalDateTime.now());
        OrderEvent savedOrderEvent = orderEventRepository.save(orderEvent);
        order.setOrderStatus(OrderStatus.valueOf(status));
        order.getOrderEvents().add(savedOrderEvent);
        return orderRepository.save(order);
    }

    public OrderStatusDTO getOrderStatus(Long orderId) throws OrderNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with " + orderId +" not found"));
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderId(orderId);
        orderStatusDTO.setStatus(order.getOrderStatus().toString());
        return orderStatusDTO;
    }
}
