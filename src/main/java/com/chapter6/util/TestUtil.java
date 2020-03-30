package com.chapter6.util;


import com.chapter6.mapper.TestMapper;
import com.chapter6.mapper.TestRecordMapper;
import com.chapter6.mapper.UriMapper;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.ExpectMap;
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
    @Autowired
    private ExpectUtil expectUtil;

    /**
     * 门店端登入数据
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


        /**
         * 登入数据的演算
         * @1、获取登入接口
         * @2、区分验证码/密码
         */

        //-----------------------获取登入接口--------------------
        RequestUri loginUri = uriMapper.getUriById(0);
        //---------------------------------------------------


        RequestTestCase loginTestCase = new RequestTestCase();
        JSONObject webformOfTest = new JSONObject();


        //----------------------------密码/验证码-------------------------
        String mobile = data.getDoTest().getStoreAccount();
        String storePassword = data.getDoTest().getStorePassword();
        String password = "";
        String grantType = "store_password";
        String storeCodeword = data.getDoTest().getStoreCodeword();
        String passwordType = "password";
        if (storePassword.length() > 0) {
            password = DigestUtils.md5DigestAsHex(storePassword.getBytes());
        } else {
            grantType = "sms_code";
            passwordType = "sms_code";
            if ("8888".equals(storeCodeword)) {
                getCode(mobile, environment);
                password = DigestUtils.md5DigestAsHex(storeCodeword.getBytes());
            }
        }
        //-----------------------------------------------------------------



        //------------------------存放登入数据-------------------------------
        webformOfTest.put("mobile", mobile);
        webformOfTest.put(passwordType,password);
        webformOfTest.put("grant_type",grantType);
        loginTestCase.setWebform(webformOfTest.toString());
        //----------------------------------------------------------------



        loginData.setTestCase(loginTestCase);
        loginData.setUri(loginUri);
        loginData.setDoTest(loginDoTest);
        loginData.setUri(uriMapper.getUriById(0));


        //获取response

        Map<String, String> response = apiUtil.getResponse(loginData, apiUtil.getLoginBasic(data));
        /*发送请求*/
        JSONObject result = verification.stringToJsonObject(
                apiUtil.getResult(response)
        );
        if (apiUtil.getStatus(response).equals("200")) {

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
        String status = apiUtil.getStatus(response);

        ExpectMap expectMap = expectUtil.expectResult(status, responseValueString, data.getTestCase());


        //期望值的验证
        if (expectMap.getStatusMsg().equals("true")) {


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


        requestRecordTest.setUserGroupId(userGroupId);
        requestRecordTest.setRecordId(record);
        requestRecordTest.setTestcaseId(data.getTestCase().getTestCaseId());
        requestRecordTest.setResult(1);
        requestRecordTest.setResponse(apiUtil.getResult(response));
        requestRecordTest.setUriId(data.getUri().getUriId());


        testRecordMapper.insert(requestRecordTest);
        expectUtil.responseResultExpect(expectMap, data);


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
        /**
         * 多个用户的登入情况演算
         * 根据传入的账户类型
         * @storeAccount 门店账户/取货点
         * @storePassword 门店账户密码
         * @StoreCodeword 门店账户验证码
         * @driverAccount 司机账户
         * @driverCodeword 司机账户验证码
         * @ReStoreAccout 取货方账户
         * @ReStorePassword 取货方账户密码
         * @ReStoreCodeword 取货方账户验证码
         * @ServiceAccount 服务车账号
         * @ServicePassword 服务车账号密码
         * @ServiceCodeword 服务车账号验证码
         */
        RequestDoTest loginDoTest = doTest;
        loginDoTest.setTestCaseId(Integer.parseInt(testIdList[0]));

        doTest.setTestCaseId(Integer.parseInt(testIdList[0]));
        ApiUtilData loginData = apiUtil.getData(loginDoTest);

        String tokenAndUserGroupId = getToken(loginData, record);
        if (tokenAndUserGroupId.length() < 4) {
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
            if (verification.isEmpty(rely)) {
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
            String status = apiUtil.getStatus(response);
            ExpectMap expectMap = expectUtil.expectResult(status, responseValueString, data.getTestCase());

            //状态码期望值的验证

            if ("200".equals(apiUtil.getStatus(response))) {
                if (data.getUri().getSave().length() > 0) {
                    JSONObject saves = verification.stringToJsonObject(data.getUri().getSave());
                    JSONObject saveValues = new JSONObject();
                    Iterator<String> save = saves.keys();

                    /**
                     * 存入依赖的值
                     */
                    while (save.hasNext()) {
                        String saveName = save.next();
                        String saveWay = saves.get(saveName).toString();
                        Object values = JsonPath.read(responseValueString, saveWay);
                        values = getRelyValue(values);
                        saveValues.put(saveName, values);
                    }
                    requestRecordTest.setValue(saveValues.toString());
                }
            }


            //返回值期望的验证
            if (data.getTestCase().getApi() == 1) {
                requestRecordTest.setResponseValueExpect(apiUtil.isResponseValueExpect(responseValueString, data.getTestCase().getApis()));
            }


            requestRecordTest.setUserGroupId(userGroupId);
            requestRecordTest.setRecordId(record);
            requestRecordTest.setTestcaseId(data.getTestCase().getTestCaseId());
            requestRecordTest.setResult(1);
            requestRecordTest.setResponse(apiUtil.getResult(response));
            requestRecordTest.setUriId(data.getUri().getUriId());


            testRecordMapper.insert(requestRecordTest);
            expectUtil.responseResultExpect(expectMap, data);


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
            if (rely != null && !rely.equals("")) {
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
    public Map<String, String> getCode(String telephone, int environment) throws Throwable {

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
        return apiUtil.getResponse(getCodeData, "0");
    }

    /**
     * 对获取值的转义
     */
    public Object getRelyValue(Object o) {
        String s = o + "";
        if (s.contains("[")) {
            Object obj = null;
            JSONArray array = new JSONArray(s);
            if (array.length() < 2) {
                obj = array.get(0).toString();
                return obj;
            }
        }
        return o;

    }


}
