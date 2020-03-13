package com.chapter6.util;


import com.chapter6.mapper.TestRecordMapper;
import com.chapter6.mapper.UriMapper;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.request.RequestDoTest;
import com.chapter6.model.request.RequestRecordTest;
import com.chapter6.model.request.RequestTestCase;
import com.chapter6.model.request.RequestUri;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
public class TestUtil {
    @Autowired
    private ApiUtil apiUtil;
    @Autowired
    private UriMapper uriMapper;
    @Autowired
    private Verification verification;
    @Autowired
    private TestRecordMapper testRecordMapper;

    /**
     * 登入+获取Token 或者
     */
    public String getToken(ApiUtilData data,long record) throws Throwable {

        long userGroupId =System.currentTimeMillis();
        RequestRecordTest requestRecordTest = new RequestRecordTest();
        requestRecordTest.setTestcaseId(0);
        requestRecordTest.setRecordId(record);
        requestRecordTest.setUserGroupId(userGroupId);
        requestRecordTest.setUriId(0);

        ApiUtilData loginData = new ApiUtilData();
        RequestDoTest loginDoTest = new RequestDoTest();
        loginDoTest.setEnvironment(
                data.getDoTest().getEnvironment()
        );
        RequestUri loginUri = uriMapper.getUriById(5);

        RequestTestCase loginTestCase = new RequestTestCase();
        JSONObject webformOfTest = new JSONObject();
        webformOfTest.put("mobile", data.getDoTest().getStoreAccount());
        if (data.getDoTest().getPassword().length() > 0) {
            webformOfTest.put("grant_type", "store_password");
            webformOfTest.put("password", DigestUtils.md5DigestAsHex(
                    data.getDoTest().getPassword().getBytes()));
        }else{
            webformOfTest.put("grant_type","sms_code");
            if(data.getDoTest().getCodeword().equals("8888")){
                webformOfTest.put("smsCode",DigestUtils.md5DigestAsHex(
                        data.getDoTest().getCodeword().getBytes()));
            }
        }
        loginTestCase.setWebform(webformOfTest.toString());

        loginData.setTestCase(loginTestCase);
        loginData.setUri(loginUri);
        loginData.setDoTest(loginDoTest);
        loginData.setUri(uriMapper.getUriById(0));


        //获取response

        Map<String,String> response = apiUtil.getResponse(loginData, apiUtil.getLoginBasic(data));
        /*发送请求*/
        JSONObject result = verification.stringToJsonObject(
                apiUtil.getResult(response)
        );




        requestRecordTest.setResult(1);
        requestRecordTest.setResponse(result.toString());
        JSONObject saves = new JSONObject();
        saves.put("userId",result.get("userId").toString());
        saves.put("userType",result.get("userType").toString());
        requestRecordTest.setValue(saves.toString());


        String tokenAndUserGroupId = result.getString("access_token")+","+userGroupId;
        if(result.has("access_token")){

        }

        testRecordMapper.insert(requestRecordTest);

        return tokenAndUserGroupId;
    }

    /**
     * 执行但接口执行一次用例
     */
    public List<Long> doTestOnce(RequestDoTest doTest) throws Throwable {
        long record = System.currentTimeMillis();
        ApiUtilData data = apiUtil.getData(doTest);
        String tokenAndUserGroupId =getToken(data,record);

        //获取token
        String token = "bearer "+tokenAndUserGroupId.split(",")[0];

        RequestRecordTest requestRecordTest = new RequestRecordTest();

        //获取用户组ID
        long userGroupId = Long.parseLong(tokenAndUserGroupId.split(",")[1]);

        //把对应的recordID和userGroupId 存入data 获取对应组的测试依赖和token
        requestRecordTest.setRecordId(record);
        requestRecordTest.setUserGroupId(userGroupId);
        data.setRequestRecordTest(requestRecordTest);


        Map<String,String> response = apiUtil.getResponse(data,token);
        String responseValueString = apiUtil.getResult(response);


        //存放需要的值
        if(data.getUri().getSave().length()>0){
            JSONObject saves = verification.stringToJsonObject(data.getUri().getSave());
            JSONObject saveValues = new JSONObject();
            Iterator<String> save = saves.keys();
            while (save.hasNext()){
                String saveName = save.next();
                String saveWay = saves.get(saveName).toString();
                Object values = JsonPath.read(responseValueString,saveWay);
                saveValues.put(saveName,values);
            }



            requestRecordTest.setValue(saveValues.toString());
        }


        requestRecordTest.setUserGroupId(userGroupId);
        requestRecordTest.setRecordId(record);
        requestRecordTest.setTestcaseId(data.getTestCase().getTestCaseId());
        requestRecordTest.setResult(1);
        requestRecordTest.setResponse(apiUtil.getResult(response));
        requestRecordTest.setUriId(data.getUri().getUriId());

        //期望值的验证
        if(data.getTestCase().getExpectOfStatus()==3){
            requestRecordTest.setStatusExpect(apiUtil.isStatus(data,apiUtil.getStatus(response)));
        }


        testRecordMapper.insert(requestRecordTest);


        System.out.println(responseValueString);
        List<Long> list  = new ArrayList<>();
        list.add(data.getRequestRecordTest().getRecordId());
        list.add(data.getRequestRecordTest().getUserGroupId());
        return list;
    }

    /**
     * 测试用例组的查询
     */
    public void doGroupTest(RequestDoTest doTest,Long record) throws Throwable {
        String testIdGroup = doTest.getTestIdGroup();
        String[] testIdList =  testIdGroup.split(",");
        ApiUtilData loginData =  new ApiUtilData();
        doTest.setTestCaseId(Integer.parseInt(testIdList[0]));
        loginData.setDoTest(doTest);
        String tokenAndUserGroupId =getToken(loginData,record);

        //获取token
        String token = "bearer "+tokenAndUserGroupId.split(",")[0];

        //获取用户组ID
        long userGroupId = Long.parseLong(tokenAndUserGroupId.split(",")[1]);

        for(String testId : testIdList){



            RequestRecordTest requestRecordTest = new RequestRecordTest();
            requestRecordTest.setRecordId(record);
            requestRecordTest.setUserGroupId(userGroupId);


            RequestDoTest doTestOne =   doTest;
            doTestOne.setTestCaseId(Integer.parseInt(testId));

            ApiUtilData data = apiUtil.getData(doTest);
            data.setRequestRecordTest(requestRecordTest);

            Map<String,String> response = apiUtil.getResponse(data,token);
            String responseValueString = apiUtil.getResult(response);

            //存放需要的值
            if(data.getUri().getSave().length()>0){
                JSONObject saves = verification.stringToJsonObject(data.getUri().getSave());
                JSONObject saveValues = new JSONObject();
                Iterator<String> save = saves.keys();
                while (save.hasNext()){
                    String saveName = save.next();
                    String saveWay = saves.get(saveName).toString();
                    Object values = JsonPath.read(responseValueString,saveWay);
                    saveValues.put(saveName,values);
                }
                requestRecordTest.setValue(saveValues.toString());
            }

            requestRecordTest.setUserGroupId(userGroupId);
            requestRecordTest.setRecordId(record);
            requestRecordTest.setTestcaseId(data.getTestCase().getTestCaseId());
            requestRecordTest.setResult(1);
            requestRecordTest.setResponse(apiUtil.getResult(response));
            requestRecordTest.setUriId(data.getUri().getUriId());

            //期望值的验证
            if(data.getTestCase().getExpectOfStatus()==3){
                requestRecordTest.setStatusExpect(apiUtil.isStatus(data,apiUtil.getStatus(response)));
            }


            testRecordMapper.insert(requestRecordTest);


            System.out.println(responseValueString);

        }



    }

    /**
     * 存储测试记录判断id值
     */

}
