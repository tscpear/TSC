package com.chapter6.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chapter6.baseController.ResponseJson;
import com.chapter6.mapper.TestRecordMapper;
import com.chapter6.model.request.RequestDoTest;
import com.chapter6.model.request.RequestRecordTest;
import com.chapter6.model.response.ResponseRecordTest;
import com.chapter6.util.TestUtil;
import com.chapter6.util.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("dotest")
public class DoTestController {

    @Autowired
    private TestUtil testUtil;
    @Autowired
    private TestRecordMapper testRecordMapper;
    @Autowired
    private ResponseJson responseJson;

    @PostMapping("/once")
    @ResponseBody
    public JSONObject once(RequestDoTest doTest) throws Throwable {

        JSONObject obj = new JSONObject();
        RequestRecordTest requestRecordTest = new RequestRecordTest();
        requestRecordTest.setTestcaseId(doTest.getTestCaseId());


        List<Long> list = testUtil.doTestOnce(doTest);


        requestRecordTest.setRecordId(list.get(0));
        requestRecordTest.setUserGroupId(list.get(1));
        RequestRecordTest record = testRecordMapper.getTestRecord(requestRecordTest);
        JSONObject responseValue = JSONObject.parseObject(record.getResponse());

        //状态码，断言结果
        String statusExpect = record.getStatusExpect();
        obj.put("statusExpect", statusExpect);

        //返回值结果打印
        String pretty = JSON.toJSONString(responseValue, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        obj.put("responseValue", pretty);
        return responseJson.getRequest(obj);
    }

    @PostMapping("/group")
    @ResponseBody
    public JSONObject group(RequestDoTest doTest) throws Throwable {
        long recordId = System.currentTimeMillis();
        String msg = testUtil.doGroupTest(doTest,recordId);
        if("true".equals(msg)){
            List<RequestRecordTest> recordTestList = testRecordMapper.getTestRecordByRecord(recordId);
            JSONArray array = new JSONArray();
            for(RequestRecordTest recordTest : recordTestList){
                if(recordTest.getTestcaseId()!=0){
                    JSONArray obj = new JSONArray();
                    obj.add(0,recordTest.getTestcaseId());
                    obj.add(1,recordTest.getStatusExpect());
                    array.add(obj);
                }

            }

            return responseJson.findList(array,0);
        }else{
            return responseJson.getMsg(msg,1);
        }

    }
}
