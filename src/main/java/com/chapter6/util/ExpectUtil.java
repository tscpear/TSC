package com.chapter6.util;

import com.chapter6.mapper.TestRecordMapper;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.ExpectMap;
import com.chapter6.model.request.RequestRecordTest;
import com.chapter6.model.request.RequestTestCase;
import com.jayway.jsonpath.JsonPath;
import org.jcp.xml.dsig.internal.dom.ApacheData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.*;
@Service
public class ExpectUtil {
    @Autowired
    private TestRecordMapper testRecordMapper;
    @Autowired
    private Verification verification;


    /**
     * 期望值断言结果的存放
     */
    public void responseResultExpect(ExpectMap map, ApiUtilData data) throws Throwable {
        long recordId = data.getRequestRecordTest().getRecordId();
        int testcaseId = data.getTestCase().getTestCaseId();
        long userGroupId = data.getRequestRecordTest().getUserGroupId();
        String statusExpectResult = map.getStatusMsg();
        String responseExpectResult = map.getResponseMsg();
        String sqlExpectResult = map.getSqlMsg();
        testRecordMapper.updataExpectResult(statusExpectResult, responseExpectResult, sqlExpectResult, recordId, testcaseId, userGroupId);


    }

    /**
     * 期望值对比
     */

    public ExpectMap expectResult(String status, String response, RequestTestCase testCase) throws Throwable {
        ExpectMap map = new ExpectMap();
        map.setResponseMsg("true");
        map.setStatusMsg("true");
        map.setSqlMsg("true");

        //状态码期望值 断言
        if (verification.isEmpty(status)) {
            map.setStatusMsg("请求失败");
            return map;
        }
        String statusExpect = testCase.getStatus().toString();
        if (!verification.isEmpty(statusExpect)) {
            if (!status.equals(statusExpect)) {
                map.setStatusMsg("状态码期望值为：" + statusExpect + ";实际为：" + status);
            }
        }
        //返回值期望值 断言
        String responseExpect = testCase.getApis();
        if (!verification.isEmpty(responseExpect)) {
            if (verification.isEmpty(response)) {
                map.setResponseMsg("返回值为空");
                return map;
            }
            JSONObject respectExpectObj = verification.stringToJsonObject(responseExpect);
            Iterator<String> obj = respectExpectObj.keys();
            while (obj.hasNext()) {
                String way = obj.next();
                Object valueExpect = respectExpectObj.get(way);
                Object value = "";
                if (way.contains("[]")) {
                    value = new ArrayList<>();
                }
                value = parse(response).read(way);
                if (verification.isEmpty(value)) {
                    map.setResponseMsg("返回值中没有字段" + way);
                    return map;
                }
                if (!value.equals(valueExpect)) {
                    map.setResponseMsg(way + "的期望值：" + valueExpect + ";实际返回值为：" + value);
                    return map;
                }

            }
        }
        return map;
    }

}
