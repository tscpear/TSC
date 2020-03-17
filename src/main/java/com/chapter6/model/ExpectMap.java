package com.chapter6.model;

import lombok.Data;

@Data
public class ExpectMap {
    private String StatusMsg;
    private boolean status;
    private boolean response;
    private String responseMsg;
    private boolean sql;
    private String sqlMsg;
}
