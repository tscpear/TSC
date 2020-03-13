package com.chapter6.model;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Ss {
    private String orderSn;
    private Integer orderType;
    private Integer testDataPoint;
}
