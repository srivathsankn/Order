package com.srivath.cart.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private String userName;
    private String email;

    User()
    {

    }

    User(String userName, String email)
    {
        this.userName = userName;
        this.email = email;
    }

    User(String emailId)
    {
        this.email = emailId;
    }
}
