package com.chapter6.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test {
    private Integer id;
    private String name;
    private int uriId;
    private String apiVariable;
    private String apiValue;
    private String headerVariable;
    private String headerValue;
    private String passParameterVariable;
    private String passParameterValue;
    private String expectedType;
    private String expected;
    private String depend;
    private String responseStore;
    private int dependent;
}
