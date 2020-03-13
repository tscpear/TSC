package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestDoTest {
    private String storeAccount;
    private String truckAccount;
    private String password;
    private String codeword;
    private int testCaseId;
    private int environment;
    private String testIdGroup;
}
