package com.chapter6.util;

import com.chapter6.mapper.*;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.Url;
import com.chapter6.model.request.RequestDoTest;
import com.chapter6.model.request.RequestRecordTest;
import com.chapter6.model.request.RequestTestCase;
import com.chapter6.model.request.RequestUri;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ApiUtil {

    @Autowired
    private UriMapper uriMapper;
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private UrlMapper urlMapper;
    @Autowired
    private Verification verification;
    @Autowired
    private TestRecordMapper testRecordMapper;
    @Autowired
    private BasicMapper basicMapper;


    /**
     * 获取所有 数据
     */
    public ApiUtilData getData(RequestDoTest requestDoTest) {
        ApiUtilData apiUtilData = new ApiUtilData();
        apiUtilData.setDoTest(requestDoTest);
        apiUtilData.setTestCase(
                testMapper.getTestCaseById(
                        requestDoTest.getTestCaseId()));
        apiUtilData.setUri(
                uriMapper.getUriById(
                        testMapper.getTestCaseById(
                                requestDoTest.getTestCaseId()).getUriId()));
        return apiUtilData;
    }


    /**
     * 合成测试API
     */
    public String getTestApi(ApiUtilData apiUtilData) {
        int device = apiUtilData.getUri().getDevice();
        int environment = apiUtilData.getDoTest().getEnvironment();
        String uri = apiUtilData.getUri().getUri();
        String api = "";
        Url url = urlMapper.getUrl(device);
        switch (environment) {
            case 1:
                api = url.getUat() + uri;
                break;
            case 2:
                api = url.getIntegrationMajor() + uri;
                break;
            case 3:
                api = url.getIntegrationMinor() + uri;
                break;
            case 4:
                api = url.getQaMajor() + uri;
                break;
            case 5:
                api = url.getQaMinor() + uri;
                break;
            case 6:
                api = url.getIntegrationMajor() + uri;
                break;
            case 7:
                api = url.getProd() + uri;
                break;
        }

        switch (apiUtilData.getUri().getUripar()) {
            //接口末尾无参数
            case 0:
                api = api;
                break;
            //接口末尾固定参数
            case 1:
                api = api + "/" + apiUtilData.getUri().getUrivar();
                break;
            //接口末尾参数来自依赖的接口
            case 2:
                api = api + "/" + "";
                break;
            //接口末尾参是来自测试用例的手动调整
            case 3:
                api = api + "/" + apiUtilData.getTestCase().getApivar();
        }
        System.err.println(api);
        return api;
    }

    /**
     * 合成登入的API
     */
    public void getLoginApi(ApiUtilData apiUtilData) {

    }

    /**
     * 通过设备和环境 获取登入接口中需要的Basic
     */
    public String getLoginBasic(ApiUtilData apiUtilData) {
        int device = apiUtilData.getUri().getDevice();
        int environment = apiUtilData.getDoTest().getEnvironment();


        //管理后台 登入的
        if(environment == 1 || environment ==7){
            return basicMapper.basic(1,device);
        }else{
            return basicMapper.basic(2,device);
        }

       /* if (device == 1) {
            if (environment == 1 || environment == 7) {
                return "Basic TUFOQUdFTUVOVDo2NTRjMzRkMGFkZTc0N2VlYTBmZDgzZmI5N2JlNzI4MA==";
            } else {
                return "Basic TUFOQUdFTUVOVDpjZGU1ZDQ5OGRmNWNkMGJmMTdlY2ZiYmJkZGY5M2E0Nw==";
            }
            //门店APP
        } else if (device == 2) {
            if (environment == 1) {
                return "Basic TU9CSUxFX1NFUlZJQ0VfQ0FSOjU4NjgzZmU4ZWU2MDRmN2I5MTlhYzM0YTFmMjVkOGUy";
            } else if (environment == 7) {
                return "Basic TU9CSUxFX1NFUlZJQ0VfQ0FSOjU4NjgzZmU4ZWU2MDRmN2I5MTlhYzM0YTFmMjVkOGUy";
            } else {
                return "Basic TU9CSUxFX1NFUlZJQ0VfQ0FSOjEzNDgwNWRiZWIyODZhZjk3NTRjNTM5ODIzZGJmMTI4";
            }
            //司机端
        } else if (device == 4) {
            if (environment == 1) {
                return "Basic VFJVQ0tFUjo2NDVjOGE5MjllNmM0MTJjOWZlYmY5MGIyYWQwNzM3NA==";
            } else if (environment == 7) {
                return "Basic ";
            } else {
                return "Basic Q1VTVE9NRVJfU0VSVklDRTpvTnZsSmE3VW5JQXlKWTlnaFlaQVFxVHlpNmV1NE8=";
            }
        }
        return null;*/
    }


    /**
     * 获取请求头上的application
     */
    public String getApplication(ApiUtilData apiUtilData) {
        String application = "";
        switch (apiUtilData.getUri().getDevice()) {
            case 1:
                application = "";
                break;
            case 2:
                application = "";
                break;
            case 3:
                application = "";
                break;
            case 4:
                application = "";
                break;
            case 5:
                application = "";
                break;
            case 6:
                application = "";
                break;
            case 7:
                application = "";
                break;
        }


        return application;
    }

    /**
     * 获取header 上面参数的jsonObject 合集
     */
    public JSONObject getHeadObj(ApiUtilData apiUtilData) throws Throwable {
        //获取请求头的固定参数，来自表uri表
        JSONObject head1 = verification.stringToJsonObject(apiUtilData.getUri().getHeadtext1());
        //获取请求头的自动参数，来自
        JSONObject head2 = verification.stringToJsonObject(apiUtilData.getUri().getHeadtext2());
        JSONObject head2Value = new JSONObject();
        Iterator<String> head2s = head2.keys();
        while (head2s.hasNext()) {
            String uriIdName = head2s.next();

            String uriId = uriIdName.split("\\.")[0];
            JSONObject saveValue = getSaveValue(apiUtilData, Integer.parseInt(uriId));
            String name = head2.get(uriIdName).toString();
            if (name.contains(",")) {
                JSONArray nameArray = new JSONArray(name);
                for (Object array : nameArray) {
                    head2Value.put(array.toString(), saveValue.getString(array.toString()));
                }
            } else {
                head2Value.put(name, saveValue.getString(name));
            }
        }


        //获取请求头的手动参数,来自表testcase
        JSONObject head3 = verification.stringToJsonObject(apiUtilData.getTestCase().getHeadvar());
        JSONObject head = new JSONObject();
        verification.objToObj(head, head1);
        verification.objToObj(head, head2Value);
        verification.objToObj(head, head3);
        System.err.println(apiUtilData.getTestCase().getTestMark() + head);
        return head;
    }

    /**
     * 获取boby提的请求体,格式是webform
     */
    public JSONObject getBobyOfForm(ApiUtilData apiUtilData) throws Throwable {
        JSONObject form1 = verification.stringToJsonObject(apiUtilData.getUri().getFormtext1());
        JSONObject form2 = verification.stringToJsonObject("");
        JSONObject form3 = verification.stringToJsonObject(apiUtilData.getTestCase().getWebform());
        JSONObject form = new JSONObject();
        verification.objToObj(form, form1);
        verification.objToObj(form, form2);
        verification.objToObj(form, form3);
        return form;
    }

    /**
     * 获取boby上的请求数据，格式是json
     * jsonBase:基本格式和固定参数
     * jsonRelyWay:变量的名称 依赖的接口 变量的路径
     * jsonManualWay:手动变量名 变量路径
     * jsonRelyTest:依赖的测试用例数组
     * jsonManualTest:变量的名称 变量的路径
     */
    public String getBobyOfJson(ApiUtilData apiUtilData) throws Throwable {
        JSONObject jsonBase;
        int bobyType;
        JSONObject jsonRelyWay = verification.stringToJsonObject(apiUtilData.getUri().getJsontext2());
        JSONObject jsonManualWay = verification.stringToJsonObject(apiUtilData.getUri().getJsontext3());
        String jsonRelyTest = apiUtilData.getTestCase().getRely();
        JSONObject jsonManualTest = verification.stringToJsonObject(apiUtilData.getTestCase().getJson());
        //判断boby的传参可是 数组 还是 对象
        if (verification.isJSONArray(apiUtilData.getUri().getJsontext1())) {

            JSONArray jsonBaseArray = new JSONArray(apiUtilData.getUri().getJsontext1());
            jsonBase = new JSONObject(jsonBaseArray.get(0));
            bobyType = 1;
        } else {
            jsonBase = new JSONObject(apiUtilData.getUri().getJsontext1());
            bobyType = 0;
        }


        //通过格式，使用不同的方法 注入变量,该返回值中只有对象 或者变量值时整个数组的形式存入
        if (bobyType == 0) {
            //把jsonBase jsonPath 的方式 进行 值的修改
            DocumentContext ext = JsonPath.parse(jsonBase.toString());

            //注入手动变量
            Iterator<String> jsonManaulWays = jsonManualWay.keys();
            while (jsonManaulWays.hasNext()) {
                //提取变量的名称
                String name = jsonManaulWays.next();
                //提取变量的路径
                String way = jsonManualWay.get(name).toString();
                //获取变量的值
                String value = jsonManualTest.get(name).toString();
                JsonPath p = JsonPath.compile(way);
                ext.set(p, value);
            }

            //注入自动参变量
            JSONObject relyNameValue = new JSONObject();
            Iterator<String> relyUriNameWay = jsonRelyWay.keys();
            while (relyUriNameWay.hasNext()) {
                String uriIdName = relyUriNameWay.next();
                String uriId = uriIdName.split("\\.")[0];
                JSONObject nameWays = verification.stringToJsonObject(jsonRelyWay.get(uriIdName).toString());
                Iterator<String> nameWay = nameWays.keys();
                while (nameWay.hasNext()) {
                    String name = nameWay.next();
                    String way = nameWays.getString(name);
                    JsonPath p = JsonPath.compile(way);
                    JSONObject values = getSaveValue(apiUtilData, Integer.parseInt(uriId));
                    String value = values.get(name).toString();
                        ext.set(p, value);
                }
            }
            String s = ext.jsonString();
            System.out.println(s);
            System.out.println("尼玛" + ext.jsonString());
            return ext.jsonString();
        }
        return null;

    }


    /**
     * post请求，发起请求
     */
    public HttpPost getPost(ApiUtilData apiUtilData, String authorization) throws Throwable {
        HttpPost post = new HttpPost(getTestApi(apiUtilData));
        //如果是登入接口，那就是传入basic,如果不是，就是传入token

        post.setHeader("Authorization", authorization);


        //放入请求头信息
        JSONObject headObj = getHeadObj(apiUtilData);
        Iterator<String> head = headObj.keys();
        while (head.hasNext()) {
            String key = head.next();
            post.setHeader(key, headObj.get(key).toString());
        }

        System.err.println(post.getAllHeaders().toString());

        //放入请求提系信息
        JSONObject form = getBobyOfForm(apiUtilData);
        if (apiUtilData.getUri().getWebform3() +
                apiUtilData.getUri().getWebform2() +
                apiUtilData.getUri().getWebform1() > 0) {
            List<NameValuePair> parame = new ArrayList<NameValuePair>();
            Iterator<String> forms = form.keys();
            while (forms.hasNext()) {
                String key = forms.next();
                parame.add(new BasicNameValuePair(key, form.get(key).toString()));
            }
            HttpEntity entityParam = new UrlEncodedFormEntity(parame, "utf-8");
            post.setEntity(entityParam);
        }

        //放入json格式boby
        if (apiUtilData.getUri().getJson1() +
                apiUtilData.getUri().getJson2() +
                apiUtilData.getUri().getJson3() > 0) {
            StringEntity entity = new StringEntity(getBobyOfJson(apiUtilData), "utf-8");
            System.err.println("你他妈的" + entity.toString());
            post.setEntity(entity);
        }


        Header[] headers = post.getAllHeaders();
        for (Header header : headers) {
            System.out.println(header.getName() + ":" + header.getValue());
        }
        return post;
    }

    /**
     * get请求，发起请求
     */
    public HttpGet getGet(ApiUtilData apiUtilData, String token) throws Throwable {
        HttpGet get = new HttpGet(getTestApi(apiUtilData));


        //放入请求提系信息


        if (apiUtilData.getUri().getWebform3() +
                apiUtilData.getUri().getWebform2()
                + apiUtilData.getUri().getWebform1() > 0) {
            JSONObject form = getBobyOfForm(apiUtilData);
            try {
                StringBuffer sb = new StringBuffer();
                sb.append(getTestApi(apiUtilData));
                sb.append("?");
                Iterator<String> forms = form.keys();
                int i = 0;
                while (forms.hasNext()) {
                    String key = forms.next();
                    if (i == 0) {
                        sb.append(key + "=" + form.get(key));
                        i++;
                    } else {
                        sb.append("&" + key + "=" + form.get(key));
                    }
                }
                get = new HttpGet(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (apiUtilData.getUri().getJson1() +
                apiUtilData.getUri().getJson2() +
                apiUtilData.getUri().getJson3() > 0) {


        }

        //放置请求头
        get.setHeader("Authorization", token);
        JSONObject headObj = getHeadObj(apiUtilData);
        Iterator<String> head = headObj.keys();
        while (head.hasNext()) {
            String key = head.next();
            get.setHeader(key, headObj.get(key).toString());
        }
        System.out.println(get.toString() + get.getAllHeaders().toString());
        Header[] headers = get.getAllHeaders();
        for (Header header : headers) {
            System.out.println(header.getName() + ":" + header.getValue());
        }
        return get;

    }

    /**
     * put请求，发起请求
     */
    public HttpPut getPut(ApiUtilData apiUtilData, String token) throws Throwable {
        HttpPut put = new HttpPut(getTestApi(apiUtilData));
        if (!token.equals("0")) {
            put.setHeader("Authorization", token);
        }
        JSONObject headObj = getHeadObj(apiUtilData);
        Iterator<String> head = headObj.keys();
        while (head.hasNext()) {
            String key = head.next();
            put.setHeader(key, headObj.get(key).toString());
        }
        JSONObject form = getBobyOfForm(apiUtilData);
        if (apiUtilData.getUri().getWebform3() +
                apiUtilData.getUri().getWebform2() +
                apiUtilData.getUri().getWebform1() > 0) {
            List<NameValuePair> parame = new ArrayList<NameValuePair>();
            Iterator<String> forms = form.keys();
            while (forms.hasNext()) {
                String key = forms.next();
                parame.add(new BasicNameValuePair(key, form.get(key).toString()));
            }
            HttpEntity entityParam = new UrlEncodedFormEntity(parame, "utf-8");
            put.setEntity(entityParam);
        }
        if (apiUtilData.getUri().getJson1() +
                apiUtilData.getUri().getJson2() +
                apiUtilData.getUri().getJson3() > 0) {
            StringEntity entity = new StringEntity(getBobyOfJson(apiUtilData), "utf-8");
            put.setEntity(entity);
        }
        System.out.println(put.toString() + put.getAllHeaders().toString());
        Header[] headers = put.getAllHeaders();
        for (Header header : headers) {
            System.out.println(header.getName() + ":" + header.getValue());
        }
        return put;
    }


    /**
     * 发起请求，输出返回值和cookie值的合集
     */
    public Map<String, String> getResponse(ApiUtilData apiUtilData, String authorization) {
        int apiType = apiUtilData.getUri().getMethod();
        HttpPost post;
        HttpPut put;
        HttpGet get;
        Map<String, String> map = new HashMap<>();
        map.put("result", "");
        map.put("cookie", "");
        map.put("status", "");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            if (apiType == 2) {
                post = getPost(apiUtilData, authorization);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(2000).setConnectTimeout(2000).build();
                post.setConfig(requestConfig);
                response = httpclient.execute(post);
            } else if (apiType == 3) {
                put = getPut(apiUtilData, authorization);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(2000).setConnectTimeout(2000).build();
                put.setConfig(requestConfig);
                response = httpclient.execute(put);
            } else {
                get = getGet(apiUtilData, authorization);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(2000).setConnectTimeout(2000).build();
                get.setConfig(requestConfig);
                response = httpclient.execute(get);
            }

            HttpEntity httpEntity = response.getEntity();
            map.put("result", EntityUtils.toString(httpEntity, "utf-8"));
            map.put("status", response.getStatusLine().getStatusCode() + "");
            String cookie = "";
            try {
                org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");
                for (org.apache.http.Header header : headers) {
                    String cookies = header.getValue();
                    cookie = cookies.split("JSESSIONID=")[1].split(";")[0];
                }
            } catch (Exception e) {
            }
            map.put("cookie", cookie);
            System.err.println(response.toString());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            //关闭所有资源连接
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;

    }


    /**
     * 获取返回体
     */
    public String getResult(Map<String, String> map) {
        if (map == null) {
            return "没有返回值";
        }
        System.err.println(map.get("result"));
        return map.get("result");
    }

    /**
     * 获取cookie
     */
    public String getCookie(Map<String, String> map) {
        if (map == null) {
            return "没有返回值";
        }
        return map.get("cookie");

    }

    /**
     * 获取状态码
     */
    public String getStatus(Map<String, String> map) {

        if (map == null) {
            return "没有返回值";
        }
        return map.get("status");
    }

    /**
     * 通过uriId 获取到值
     */
    public JSONObject getSaveValue(ApiUtilData data, int uriId) throws Throwable {
        RequestRecordTest requestRecordTest = data.getRequestRecordTest();
        requestRecordTest.setUriId(uriId);
        JSONObject values = verification.stringToJsonObject(testRecordMapper.getValue(requestRecordTest));
        return values;
    }

    /**
     * 期望值的验证---状态码的验证
     */
    public String isStatus(ApiUtilData data, String status) {
        String expect = data.getTestCase().getStatus() + "";
        if (expect.equals(status)) {
            return "true";
        } else {
            JSONObject expected = new JSONObject();
            expected.put("expect", expect);
            expected.put("result", status);
            return expected.toString();
        }
    }


    /**
     * 期望值的验证---某些返回值的验证
     */
    public String isResponseValueExpect(String response, String expect) throws Throwable {
        JSONObject responseValueExpect = verification.stringToJsonObject(expect);
       Iterator<String> expectValue = responseValueExpect.keys();
       while (expectValue.hasNext()){
           String way = expectValue.next();
           String rValue = responseValueExpect.get(way).toString();
           String eValue = JsonPath.parse(response).read(way).toString();
           if(!rValue.equals(eValue)){

               return "false";
           }
       }
       return "true";
    }
    /**
     * 期望值的验证---数据库期望值的验证
     * @1、需要校验的字段
     * @2、需要获取数据的sql
     * @3、需要校验的公式（安置内部校验的逻辑）
     * [{校验逻辑：{值A：{常量/sql/response}}]
     */
    public String isSqlValue(ApiUtilData data) throws Throwable {
        String sqlExpectValues = data.getTestCase().getSqls();
        JSONObject sqlExpectValue = verification.stringToJsonObject(sqlExpectValues);




        return null;
    }

}
