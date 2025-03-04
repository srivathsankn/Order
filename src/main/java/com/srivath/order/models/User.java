package com.srivath.order.models;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
