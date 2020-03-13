package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestRecordTest {
    private Integer id;
    private long recordId;
    private String value;
    private Integer testcaseId;
    private String request;
    private String response;
    private Integer result;
    private String logs;
    private long userGroupId;
    private Integer uriId;
    private String responseValueExpect;
    private String sqlExpect;
    private String statusExpect;
}
