package com.srivath.order.dtos;

import com.srivath.order.models.Order;
import com.srivath.order.models.OrderStatus;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class OrderDTO {
    private Long orderId;
    private String cartId;
    private String userEmail;
    private LocalDate orderDate;
    private OrderStatus orderStatus;

    public static OrderDTO from(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setCartId(order.getCartId());
        orderDTO.setUserEmail(order.getUserEmail());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setOrderStatus(order.getOrderStatus());
        return orderDTO;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
