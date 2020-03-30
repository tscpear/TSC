package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestTestCase {
    private int testCaseId;
    private String testMark;
    private Integer uriId;
    private Integer method1 = 0;
    private Integer method2 = 0;
    private Integer method3 = 0;
    private String rely;
    private String apivar = "";
    private String headvar = "";
    private String webform = "";
    private String json = "";
    private Integer api = 0;
    private String apis;
    private Integer expectOfSql = 0;
    private String sqls;
    private String request;
    private String response;
    private int disable;
    private Integer expectOfStatus = 0;
    private Integer status;
    private Integer accountType;
}
