package com.chapter6.util;

import com.chapter6.mapper.TestRecordMapper;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.ExpectMap;
import com.chapter6.model.request.RequestRecordTest;
import com.jayway.jsonpath.JsonPath;
import org.jcp.xml.dsig.internal.dom.ApacheData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.*;

public class ExpectUtil {
    @Autowired
    private TestRecordMapper testRecordMapper;
    @Autowired
    private Verification verification;


    /**
     * 期望值断言结果的存放
     */
    public void responseResultExpect(RequestRecordTest recordTest) {


    }

    /**
     * 期望值对比
     */

    public ExpectMap statusExpect(String status, String response, RequestRecordTest requestRecordTest) throws Throwable {
        ExpectMap map = new ExpectMap();
        map.setStatus(true);
        map.setResponse(true);
        map.setSql(true);

        //状态码期望值 断言
        if (verification.isEmpty(status)) {
            map.setStatus(false);
            map.setStatusMsg("请求失败");
            return map;
        }
        String statusExpect = requestRecordTest.getStatusExpect();
        if (!verification.isEmpty(statusExpect)) {
            if (!status.equals(statusExpect)) {
                map.setStatus(false);
                map.setStatusMsg("期望值为：" + statusExpect + ";实际状态码为：" + status);
            }
        }
        //返回值期望值 断言
        String responseExpect = requestRecordTest.getResponseValueExpect();
        if (!verification.isEmpty(requestRecordTest)) {
            if (verification.isEmpty(response)) {
                map.setResponse(false);
                map.setResponseMsg("返回值为空");
                return map;
            }
            JSONObject respectExpectObj = verification.stringToJsonObject(responseExpect);
            Iterator<String> obj = respectExpectObj.keys();
            while (obj.hasNext()) {
                String way = obj.next();
                String valueExpect = respectExpectObj.get(way).toString();
                Object value = "";
                if (way.contains("[]")) {
                    value = new ArrayList<>();
                }
                value = parse(response).read(way);
                if(verification.isEmpty(value)){
                    map.setResponse(false);
                    map.setResponseMsg("返回值中没有字段"+way);
                    return map;
                }
                if(!value.equals(valueExpect)){
                    map.setResponse(false);
                    map.setResponseMsg(way+"的期望值："+valueExpect+";实际返回值为："+value);
                    return map;
                }

            }
        }
        return map;
    }

}
