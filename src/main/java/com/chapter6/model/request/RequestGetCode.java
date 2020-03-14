package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestGetCode {
    private String telephone;
    private Integer environment;
}
