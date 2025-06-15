package com.srivath.order.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String cartId;
    private String userEmail;
    private String userName;
    private String userPhone;
    private LocalDate orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private Double OrderAmount;
    @OneToMany(mappedBy = "order")
    private List<OrderEvent> orderEvents;

    public Order()
    {
        orderEvents = new ArrayList<>();
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

    public Double getOrderAmount() {
        return OrderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        OrderAmount = orderAmount;
    }

    public List<OrderEvent> getOrderEvents() {
        return orderEvents;
    }

    public void setOrderEvents(List<OrderEvent> orderEvents) {
        this.orderEvents = orderEvents;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
