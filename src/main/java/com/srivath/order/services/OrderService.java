package com.srivath.order.services;

import com.srivath.ecombasedomain.dtos.CartOrderDto;
import com.srivath.ecombasedomain.dtos.OrderDto;
import com.srivath.ecombasedomain.events.Event;
import com.srivath.ecombasedomain.events.OrderPlacedEvent;
import com.srivath.ecombasedomain.events.PlaceOrderEvent;
import com.srivath.order.dtos.OrderDTO;
import com.srivath.order.exceptions.OrderNotFoundException;
import com.srivath.order.models.Cart;
import com.srivath.order.models.Order;
import com.srivath.order.models.OrderEvent;
import com.srivath.order.models.OrderStatus;
import com.srivath.order.repositories.OrderEventRepository;
import com.srivath.order.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderEventRepository orderEventRepository;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    OrderService(OrderRepository orderRepository, OrderEventRepository orderEventRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    public Order getOrderById(Long orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with " + orderId +" not found"));
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}",groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Event event)
    {
        if (event != null && event.getEventName().equals("PLACE_ORDER"))
        {
            PlaceOrderEvent placeOrderEvent = (PlaceOrderEvent) event;
            CartOrderDto cartOrderDto = placeOrderEvent.getCartOrderDto();
            Order order = new Order();
            order.setCartId(cartOrderDto.getCartId());
            order.setOrderAmount(cartOrderDto.getTotalAmount());
            LocalDateTime orderDateTime = LocalDateTime.now();
            order.setOrderDate(orderDateTime.toLocalDate());
            order.setUserEmail(cartOrderDto.getUserEmail());
            order.setOrderStatus(OrderStatus.PENDING);
            Order savedOrder = orderRepository.save(order); //to getOrder ID

            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrder(savedOrder);
            orderEvent.setBeforeStatus(order.getOrderStatus());
            orderEvent.setAfterStatus(OrderStatus.CONFIRMED);
            orderEvent.setDateTime(orderDateTime);
            OrderEvent savedOrderEvent = orderEventRepository.save(orderEvent);

            savedOrder.setOrderStatus(OrderStatus.CONFIRMED);
            savedOrder.getOrderEvents().add(savedOrderEvent);
            Order finalOrder =orderRepository.save(savedOrder);
            pushOrderPlacedEvent(finalOrder);

        }
    }

    public void pushOrderPlacedEvent(Order order)
    {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setUserEmail(order.getUserEmail());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setCartId(order.getCartId());
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(orderDto);
        // Push the event to Kafka
        kafkaTemplate.send(topicName, orderPlacedEvent);
    }

}
