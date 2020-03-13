package com.chapter6.model.request;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CreateData {
    private String orderSn;
    private Integer orderType;
    private Integer testDataPoint;
    private Integer environment;
}

