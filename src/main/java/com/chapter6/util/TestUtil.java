package com.chapter6.util;


import com.chapter6.mapper.TestMapper;
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
import org.springframework.util.StringUtils;

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
    @Autowired
    private TestMapper testMapper;

    /**
     * 登入+获取Token 或者
     */
    public String getToken(ApiUtilData data, long record) throws Throwable {

        long userGroupId = System.currentTimeMillis();
        RequestRecordTest requestRecordTest = new RequestRecordTest();
        requestRecordTest.setTestcaseId(0);
        requestRecordTest.setRecordId(record);
        requestRecordTest.setUserGroupId(userGroupId);
        requestRecordTest.setUriId(0);

        //创建一个新的数据集合
        ApiUtilData loginData = new ApiUtilData();
        //创建一个新的  登入专用的dotest数据集合
        RequestDoTest loginDoTest = new RequestDoTest();

        //存入环境
        int environment = data.getDoTest().getEnvironment();
        loginDoTest.setEnvironment(
                environment
        );


        //获取登入接口的数据
        RequestUri loginUri = uriMapper.getUriById(0);

        RequestTestCase loginTestCase = new RequestTestCase();
        JSONObject webformOfTest = new JSONObject();
        webformOfTest.put("mobile", data.getDoTest().getStoreAccount());
        if (data.getDoTest().getPassword().length() > 0) {
            webformOfTest.put("grant_type", "store_password");
            webformOfTest.put("password", DigestUtils.md5DigestAsHex(
                    data.getDoTest().getPassword().getBytes()));
        } else {
            webformOfTest.put("grant_type", "sms_code");
            if (data.getDoTest().getCodeword().equals("8888")) {
                getCode(data.getDoTest().getStoreAccount(), environment);
            }
            webformOfTest.put("smsCode", DigestUtils.md5DigestAsHex(
                    data.getDoTest().getCodeword().getBytes()));
        }
        loginTestCase.setWebform(webformOfTest.toString());

        loginData.setTestCase(loginTestCase);
        loginData.setUri(loginUri);
        loginData.setDoTest(loginDoTest);
        loginData.setUri(uriMapper.getUriById(0));


        //获取response

        Map<String, String> response = apiUtil.getResponse(loginData, apiUtil.getLoginBasic(data));
        /*发送请求*/

        if (apiUtil.getStatus(response).equals("200")) {
            JSONObject result = verification.stringToJsonObject(
                    apiUtil.getResult(response)
            );


            requestRecordTest.setResult(1);
            requestRecordTest.setResponse(result.toString());
            JSONObject saves = new JSONObject();
            saves.put("userId", result.get("userId").toString());
            saves.put("userType", result.get("userType").toString());
            requestRecordTest.setValue(saves.toString());


            String tokenAndUserGroupId = result.getString("access_token") + "," + userGroupId;
            if (result.has("access_token")) {

            }

            testRecordMapper.insert(requestRecordTest);

            return tokenAndUserGroupId;
        } else {
            return apiUtil.getStatus(response);
        }

    }

    /**
     * 执行但接口执行一次用例
     */
    public List<Long> doTestOnce(RequestDoTest doTest) throws Throwable {
        List<Long> list = new ArrayList<>();
        long record = System.currentTimeMillis();
        ApiUtilData data = apiUtil.getData(doTest);
        String tokenAndUserGroupId = getToken(data, record);
        if (tokenAndUserGroupId.length() < 4) {
            list.add(Long.parseLong(tokenAndUserGroupId));
        }

        //获取token
        String token = "bearer " + tokenAndUserGroupId.split(",")[0];

        RequestRecordTest requestRecordTest = new RequestRecordTest();

        //获取用户组ID
        long userGroupId = Long.parseLong(tokenAndUserGroupId.split(",")[1]);

        //把对应的recordID和userGroupId 存入data 获取对应组的测试依赖和token
        requestRecordTest.setRecordId(record);
        requestRecordTest.setUserGroupId(userGroupId);
        data.setRequestRecordTest(requestRecordTest);


        Map<String, String> response = apiUtil.getResponse(data, token);

        /**
         * 此处缺一个登入成功的判断
         */
        String responseValueString = apiUtil.getResult(response);


        //期望值的验证
        if (data.getTestCase().getExpectOfStatus() == 3) {
            requestRecordTest.setStatusExpect(apiUtil.isStatus(data, apiUtil.getStatus(response)));


            if (apiUtil.getStatus(response).equals("200")) {
                if (data.getUri().getSave().length() > 0) {
                    JSONObject saves = verification.stringToJsonObject(data.getUri().getSave());
                    JSONObject saveValues = new JSONObject();
                    Iterator<String> save = saves.keys();
                    while (save.hasNext()) {
                        String saveName = save.next();
                        String saveWay = saves.get(saveName).toString();
                        Object values = JsonPath.read(responseValueString, saveWay);
                        saveValues.put(saveName, values);
                    }


                    requestRecordTest.setValue(saveValues.toString());
                }
            }


        }


        requestRecordTest.setUserGroupId(userGroupId);
        requestRecordTest.setRecordId(record);
        requestRecordTest.setTestcaseId(data.getTestCase().getTestCaseId());
        requestRecordTest.setResult(1);
        requestRecordTest.setResponse(apiUtil.getResult(response));
        requestRecordTest.setUriId(data.getUri().getUriId());


        testRecordMapper.insert(requestRecordTest);


        System.out.println(responseValueString);
        list.add(data.getRequestRecordTest().getRecordId());
        list.add(data.getRequestRecordTest().getUserGroupId());
        return list;
    }

    /**
     * 测试用例组的查询
     */
    public String doGroupTest(RequestDoTest doTest, Long record) throws Throwable {

        //测试用例id组list
        String testIdGroup = doTest.getTestIdGroup();


        String[] testIdList = testIdGroup.split(",");
        String msg = sortTestIdList(testIdList).get("msg").toString();
        if (!"true".equals(sortTestIdList(testIdList).get("msg").toString())) {
            return sortTestIdList(testIdList).get("msg").toString();
        }

        //创建给登入接口要用的dodata 取第一个测试用例的数据就好了
        RequestDoTest loginDoTest = doTest;
        loginDoTest.setTestCaseId(Integer.parseInt(testIdList[0]));

        doTest.setTestCaseId(Integer.parseInt(testIdList[0]));
        ApiUtilData loginData = apiUtil.getData(loginDoTest);

        String tokenAndUserGroupId = getToken(loginData, record);
        if(tokenAndUserGroupId.length()<4){
            return tokenAndUserGroupId;
        }

        //获取token
        String token = "bearer " + tokenAndUserGroupId.split(",")[0];

        //获取用户组ID
        long userGroupId = Long.parseLong(tokenAndUserGroupId.split(",")[1]);

        for (String testId : testIdList) {


            RequestRecordTest requestRecordTest = new RequestRecordTest();
            requestRecordTest.setRecordId(record);
            requestRecordTest.setUserGroupId(userGroupId);


            RequestDoTest doTestOne = doTest;
            doTestOne.setTestCaseId(Integer.parseInt(testId));

            ApiUtilData data = apiUtil.getData(doTest);
            data.setRequestRecordTest(requestRecordTest);
            String rely = data.getTestCase().getRely();
            Map<String, String> response = new HashMap<>();
            if (rely == null || rely.equals("")) {
                response = apiUtil.getResponse(data, token);
            } else {
                boolean relyTrue = true;
                String[] relyList = rely.split(",");
                for (String relyId : relyList) {
                    String status = testRecordMapper.getStatusByRelyTestcaseId(Integer.parseInt(rely), record);
                    if (!status.equals("true")) {
                        relyTrue = false;
                    }
                }
                if (relyTrue) {
                    response = apiUtil.getResponse(data, token);
                }
            }
            String responseValueString = apiUtil.getResult(response);

            //期望值的验证
            if (data.getTestCase().getExpectOfStatus() == 3) {
                requestRecordTest.setStatusExpect(apiUtil.isStatus(data, apiUtil.getStatus(response)));


                //存放需要的值
                if (apiUtil.getStatus(response).equals("200")) {
                    if (data.getUri().getSave().length() > 0) {
                        JSONObject saves = verification.stringToJsonObject(data.getUri().getSave());
                        JSONObject saveValues = new JSONObject();
                        Iterator<String> save = saves.keys();
                        while (save.hasNext()) {
                            String saveName = save.next();
                            String saveWay = saves.get(saveName).toString();
                            Object values = JsonPath.read(responseValueString, saveWay);
                            saveValues.put(saveName, values);
                        }
                        requestRecordTest.setValue(saveValues.toString());
                    }
                }

            }


            requestRecordTest.setUserGroupId(userGroupId);
            requestRecordTest.setRecordId(record);
            requestRecordTest.setTestcaseId(data.getTestCase().getTestCaseId());
            requestRecordTest.setResult(1);
            requestRecordTest.setResponse(apiUtil.getResult(response));
            requestRecordTest.setUriId(data.getUri().getUriId());


            testRecordMapper.insert(requestRecordTest);


            System.out.println(responseValueString);

        }
        return "true";


    }

    /**
     * 对测试用例的排序处理
     */
    public Map<String, Object> sortTestIdList(String[] testIdList) {
        Map<String, Object> map = new HashMap<>();
        List<Integer> testIds = new ArrayList<>();
        for (String testIdString : testIdList) {
            testIds.add(Integer.parseInt(testIdString));
        }


        for (Integer testid : testIds) {
            String rely = testMapper.getRelyByTestcaseId(testid);

            //看一下依赖是不是都是存在的
            if (rely != null || rely.equals("")) {
                String[] relyList = rely.split(",");
                for (String relyId : relyList) {
                    if (!testIds.contains(Integer.parseInt(relyId))) {
                        String msg = "测试用例" + testid + "的依赖用例" + relyId + "不存在！";
                        map.put("msg", msg);
                        return map;
                    }
                }
            }
        }
        map.put("msg", "true");
        return map;
    }


    /**
     * 请求获取验证码的接口
     */
    public  Map<String, String> getCode(String telephone, int environment) throws Throwable {

        //创建一个新的数据集合
        ApiUtilData getCodeData = new ApiUtilData();
        //创建一个新的  登入专用的dotest数据集合
        RequestDoTest getCodeDoTest = new RequestDoTest();
        //存入环境
        getCodeDoTest.setEnvironment(environment);


        //获取获取验证码接口的数据
        RequestUri getCodeUri = uriMapper.getUriById(1);
        JSONObject json = verification.stringToJsonObject(getCodeUri.getJsontext1());
        json.put("mobile", telephone);
        getCodeUri.setJsontext1(json.toString());
        RequestTestCase getCodeTestcase = testMapper.getTestCaseById(1);

        getCodeData.setUri(getCodeUri);
        getCodeData.setDoTest(getCodeDoTest);
        getCodeData.setTestCase(getCodeTestcase);
        return  apiUtil.getResponse(getCodeData, "0");
    }


}
