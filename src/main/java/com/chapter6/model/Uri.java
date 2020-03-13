package com.chapter6.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Uri {
    private int id;
    private String name;
    private String uri;
    private int requestMethod;
    private int headerId;
    private int headerPassParameterType;
    private String headerPassParameter;
    private int apiPassParameterType;
    private String apiPassParameter;
    private int passParameterType;
    private String passParameter;
    private String response;
    private String client;
}
