package com.chapter6.time;

import com.alibaba.fastjson.JSONArray;
import com.chapter6.baseController.ResponseJson;
import com.chapter6.mapper.TestMapper;
import com.chapter6.mapper.TestRecordMapper;
import com.chapter6.mapper.UriMapper;
import com.chapter6.model.Test;
import com.chapter6.model.request.RequestDoTest;
import com.chapter6.model.request.RequestRecordTest;
import com.chapter6.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskTime {
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private TestRecordMapper testRecordMapper;
    @Autowired
    private MailServer mailServer;
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private UriMapper uriMapper;



    public void doTest() throws Throwable {
        String text = "";
        long recordId = System.currentTimeMillis();
        RequestDoTest doTest = new RequestDoTest();
        doTest.setEnvironment(1);
        doTest.setStoreAccount("13588096710");
        doTest.setPassword("123456");
        doTest.setTestIdGroup("16,17,18,19,20,21,22,25,27,28,29,30");

        String msg = testUtil.doGroupTest(doTest,recordId);
        if("true".equals(msg)){
            List<RequestRecordTest> recordTestList = testRecordMapper.getTestRecordByRecord(recordId);
            JSONArray array = new JSONArray();
            for(RequestRecordTest recordTest : recordTestList){
                if(recordTest.getTestcaseId()!=0){
                    JSONArray obj = new JSONArray();
                    int testId = recordTest.getTestcaseId();
                    String expect = recordTest.getStatusExpect();
                    obj.add(0,testId);
                    obj.add(1,recordTest.getStatusExpect());
                    array.add(obj);
                    if(!obj.get(1).equals("true")){
                        int uriId = testMapper.getUriIdById(testId);
                        String uri = uriMapper.getUriByUriId(uriId);
                        text = "错误接口【" + uri + "】：【" + expect +"/r/n";
                    }
                }

            }
            if(!text.equals("")){
                mailServer.sendSimpleMail("515090974@qq.com","自动化接口测试错误反馈",text);
            }
        }else{
            mailServer.sendSimpleMail("515090974@qq.com","自动化接口测试错误反馈","内容：登入有问题");
        }
    }

}
