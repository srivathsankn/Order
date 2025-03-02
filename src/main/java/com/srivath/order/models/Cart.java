package com.srivath.cart.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("cart")
@Data
//@RedisHash("cart")
public class Cart implements Serializable {

    @Id
    private String id;
    private List<CartItem> cartItems;
 //   private Double totalPrice;
    private User owner;
    @CreatedDate
    private LocalDate createdOn;
    @LastModifiedDate
    private LocalDate updatedOn;
    private String status; // ACTIVE or ORDERED
    private LocalDate OrderedOn;
    private Long orderId;
    private Address deliveryAddress;
    private Set<PaymentMethods> paymentMethods;
    private Double totalAmount;



    public Cart() {
        cartItems = new ArrayList<>();
        paymentMethods = new HashSet<>();

    }



}
