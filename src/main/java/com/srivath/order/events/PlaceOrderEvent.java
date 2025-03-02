package com.srivath.cart.events;

import com.srivath.cart.models.Cart;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceOrderEvent extends Event{
    private Cart cart;

    public PlaceOrderEvent(Cart cart) {
        this.cart = cart;
        this.setEventName("PLACE_ORDER");
    }

}
