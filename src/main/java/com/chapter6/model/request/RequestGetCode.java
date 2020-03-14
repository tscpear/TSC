package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestGetCode {
    private String accountName;
    private Integer environment;
}
