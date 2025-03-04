package com.srivath.order.controllers;
import com.srivath.order.exceptions.OrderNotFoundException;
import com.srivath.order.services.OrderService;
import com.srivath.order.dtos.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) throws OrderNotFoundException {
        return ResponseEntity.ok(OrderDTO.from(orderService.getOrderById(orderId)));
    }

}
