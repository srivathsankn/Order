package com.srivath.order.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTime;
    private OrderStatus beforeStatus;
    private OrderStatus afterStatus;
    @ManyToOne
    //@JoinColumn(name = "order_id")
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public OrderStatus getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(OrderStatus beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public OrderStatus getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(OrderStatus afterStatus) {
        this.afterStatus = afterStatus;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
