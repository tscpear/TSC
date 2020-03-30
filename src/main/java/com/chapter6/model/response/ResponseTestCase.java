package com.chapter6.model.response;

import lombok.Data;

@Data
public class ResponseTestCase {
    private int testCaseId;
    private String testMark;
    private Integer uriId;
    private Integer method1 = 0;
    private Integer method2 = 0;
    private Integer method3 = 0;
    private String rely;
    private String byRely;
    private String apivar = "";
    private String headvar = "";
    private String webform = "";
    private String json = "";
    private Integer api;
    private String apis;
    private Integer expectOfSql;
    private String sqls;
    private String request;
    private String response;
    private int method;
    private String uri;
    private int disable;
    private Integer device;
    private Integer expectOfStatus;
    private Integer status;
    private Integer accountType;
}
