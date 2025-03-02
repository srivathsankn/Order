package com.srivath.cart.events;

import com.srivath.cart.dtos.OrderDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderPlacedEvent extends Event {
    private OrderDto orderDto;

    public OrderPlacedEvent(OrderDto orderDto) {
        this.orderDto = orderDto;
        this.setEventName("ORDER_PLACED");
    }
}
