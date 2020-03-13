package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestUser {
    private Integer id;
    private String username;
    private String password;
    private String token;

}
