package com.srivath.cart.models;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class Product implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String image;
}
