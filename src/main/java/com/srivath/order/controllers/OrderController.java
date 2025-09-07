package com.srivath.order.controllers;
import com.srivath.order.dtos.OrderStatusDTO;
import com.srivath.order.exceptions.OrderNotFoundException;
import com.srivath.order.exceptions.OrderStatusChangeException;
import com.srivath.order.models.Order;
import com.srivath.order.services.OrderService;
import com.srivath.order.dtos.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/id/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) throws OrderNotFoundException {
        return ResponseEntity.ok(OrderDTO.from(orderService.getOrderById(orderId)));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<Order>> getOrderByUserEmail(@PathVariable String email) throws OrderNotFoundException {
        return ResponseEntity.ok(orderService.getOrderByUserEmail(email));
    }

    @PatchMapping("/status")
    public ResponseEntity<Order> updateOrderStatus(@RequestBody OrderStatusDTO orderStatusDTO) throws OrderNotFoundException, OrderStatusChangeException {
            return ResponseEntity.ok(orderService.updateOrderStatus(orderStatusDTO.getOrderId(), orderStatusDTO.getStatus()));
    }

    @GetMapping("/status")
    public ResponseEntity<OrderStatusDTO> getOrderStatus(@RequestBody OrderStatusDTO orderStatusDTO) throws OrderNotFoundException {
        return ResponseEntity.ok(orderService.getOrderStatus(orderStatusDTO.getOrderId()));
    }
}
