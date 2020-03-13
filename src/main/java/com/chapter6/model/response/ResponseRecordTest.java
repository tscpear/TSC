package com.chapter6.model.response;

import lombok.Data;

@Data
public class ResponseRecordTest {
    private Integer id;
    private long recordId;
    private String value;
    private Integer testcaseId;
    private String request;
    private String response;
    private Integer result;
    private String logs;
    private long userGroupId;
    private String responseValueExpect;
    private String sqlExpect;
    private String statusExpect;
}
