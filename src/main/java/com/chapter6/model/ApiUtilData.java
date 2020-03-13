package com.chapter6.model;

import com.chapter6.model.request.RequestDoTest;
import com.chapter6.model.request.RequestRecordTest;
import com.chapter6.model.request.RequestTestCase;
import com.chapter6.model.request.RequestUri;
import lombok.Data;

@Data
public class ApiUtilData {
    private RequestUri uri;
    private RequestTestCase testCase;
    private RequestDoTest doTest;
    private RequestRecordTest requestRecordTest;
}
