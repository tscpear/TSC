package com.chapter6.model.request;

import lombok.Data;

@Data
public class ListParam {
    private int pageBegin;
    private int PageEnd;
    private String uriValue;
    private String testMark;
    private String uriMark;
    private Integer device;
}
