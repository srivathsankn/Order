package com.srivath.cart.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItem implements Serializable {
    private Product product;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
