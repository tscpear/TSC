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
        doTest.setStorePassword("123456");
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
                    array.add(obj);
                    if(!"true".equals(expect)){
                        String response = recordTest.getResponse();
                        int uriId = testMapper.getUriIdById(testId);
                        String uri = uriMapper.getUriByUriId(uriId);
                        text = "错误接口【" + uri + "】：【" + expect + "】【" + response;
                    }
                }

            }
            if(!text.equals("")){
                mailServer.sendSimpleMail("515090974@qq.com","准生产",text);
                mailServer.sendSimpleMail("513653423@qq.com","准生产",text);
            }
        }else{
            mailServer.sendSimpleMail("515090974@qq.com","准生产","内容：登入有问题" + msg);
            mailServer.sendSimpleMail("513653423@qq.com","准生产","内容：登入有问题"+ msg);
        }
        }


}
