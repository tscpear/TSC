package com.chapter6.model;

import lombok.Data;

@Data
public class Url {
    private int id;
    private String name;
    private String integrationMinor;
    private String integrationMajor;
    private String qaMajor;
    private String qaMinor;
    private String uat;
    private String prod;
}
