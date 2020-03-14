package com.chapter6.controller;

import com.alibaba.fastjson.JSONPObject;
import com.chapter6.baseController.ResponseJson;
import com.chapter6.mapper.TestMapper;
import com.chapter6.mapper.UriMapper;
import com.chapter6.model.request.ListParam;
import com.chapter6.model.request.RequestTestCase;
import com.chapter6.model.request.RequestUri;
import com.chapter6.model.response.ResponseUri;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.chapter6.util.Verification;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("uri")
public class UriController {

    @Autowired
    private UriMapper uriMapper;
    @Autowired
    private ResponseJson responseJson;
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private Verification verification;


    @GetMapping("/list")
    @ResponseBody
    public com.alibaba.fastjson.JSONObject uriList(@RequestParam int page,
                                                   @RequestParam int limit,
                                                   @RequestParam(required = false) Integer device,
                                                   @RequestParam(required = false) String uriValue,
                                                   @RequestParam(required = false) String uriMark) throws Throwable {
        ListParam param = new ListParam();
        param.setPageBegin(page*limit-limit);
        param.setPageEnd(page*limit);
        param.setUriValue(uriValue);
        param.setUriMark(uriMark);
        param.setDevice(device);
            List<RequestUri> uriList = uriMapper.uriList(param);
        JSONArray array = new JSONArray();
        for (RequestUri uri : uriList) {
            ResponseUri responseUri = new ResponseUri();
            BeanUtils.copyProperties(uri, responseUri);
            int testNum = testMapper.findCountByApi(uri.getUriId());
            responseUri.setTestNum(testNum);
            JSONObject relyCase = new JSONObject();
            if (uri.getUripar() == 2) {
                relyCase(relyCase, uri.getUrivar());
            }
            if (uri.getHead2() == 2) {
                relyCase(relyCase, uri.getHeadtext2());
            }
            if (uri.getWebform2() == 2) {
                relyCase(relyCase, uri.getFormtext2());
            }
            if (uri.getJson2() == 2) {
                relyCase(relyCase, uri.getJsontext2());
            }
            responseUri.setByRely(relyCase.toString());
            array.add(responseUri);
        }
        return responseJson.findList(array, 1);
    }

    @PostMapping("/updata")
    @ResponseBody
    public JSONObject updataUri(RequestUri uri) throws Throwable {
        if (isUriValue(uri).equals("true")) {
            uriNameToId(uri);
            uriMapper.updataUri(uri);
            return responseJson.addMsg(0, "");
        } else {
            return responseJson.addMsg(1, isUriValue(uri));
        }
    }


    @PostMapping("/add")
    @ResponseBody
    public com.alibaba.fastjson.JSONObject addUri(RequestUri uri) throws Throwable {

        if (isUriValue(uri).equals("true")) {
            uriNameToId(uri);
            uriMapper.insertUri(uri);
            return responseJson.addMsg(0, "");
        } else {
            return responseJson.addMsg(1, isUriValue(uri));
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JSONObject delUri(int uriId) {
        uriMapper.delUri(uriId);
        return responseJson.getMsg("删除成功",0);
    }

    /**
     * 存放relyCase
     *
     * @param relyCase
     * @param api
     */
    public void relyCase(JSONObject relyCase, String api) throws Throwable {
        org.json.JSONObject value = verification.stringToJsonObject(api);
        if (value != null) {
                Iterator<String> list = value.keys();
            String key = "";
            JSONArray array = new JSONArray();
            while (list.hasNext()) {
                key = list.next();
                int uriId = Integer.parseInt(key.split("[.]")[0]);
                List<RequestTestCase> testCaseList = testMapper.findTestCaseByUriId(uriId);
                for (int i = 0; i < testCaseList.size(); i++) {
                    array.add(testCaseList.get(i).getTestCaseId());
                }
            }
            relyCase.put(key, array);

        }
    }

    /**
     * 判断 是不是jsonObject
     */
    public boolean isObject(String s) {
        try {
            org.json.JSONObject value = new org.json.JSONObject(s);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断uri返回值中 的值是否符合规则
     */
    public String isUriValue(RequestUri uri) throws Throwable {
        if (uri.getUripar() == 2) {
            if (!isObject(uri.getUrivar())) {
                return "接口变量格式错误";
            }
        }
        if (uri.getHead1() == 1) {
            if (!isObject(uri.getHeadtext1())) {
                return "头固定变量格式错误";
            }
        }
        if (uri.getHead2() == 2) {
            if (!isObject(uri.getHeadtext2())) {
                return "头自动变量格式错误";
            }
        }
        if (uri.getHead3() == 3) {
            if (!isObject(uri.getHeadtext3())) {
                return "头手动参数格式错误";
            }
        }
        if (uri.getWebform1() == 1) {
            if (!isObject(uri.getFormtext1())) {
                return "form固定变量格式错误";
            }

        }
        if (uri.getWebform2() == 2) {
            if (!isObject(uri.getFormtext2())) {
                return "form自动变量格式错误";
            }else{
                org.json.JSONObject obj = verification.stringToJsonObject(uri.getFormtext2());
                Iterator<String> objs = obj.keys();
                while (objs.hasNext()){
                    String array = objs.next();
                    if(!verification.isJSONArray(array)){
                        return "form自动变量格式错误";
                    }
                }
            }
        }
        if (uri.getWebform3() == 3) {
            if (!isObject(uri.getFormtext3())) {
                return "form手动参数格式错误";
            }
        }
        if (uri.getJson1() == 1) {
            if (!isObject(uri.getJsontext1())) {
                return "json固定参数格式错误";
            }
        }
        if (uri.getJson2() == 2) {
            if (!isObject(uri.getJsontext2())) {
                return "json自动参数格式错误";
            }
        }
        if (uri.getJson3() == 3) {
            if (!isObject(uri.getJsontext3())) {
                return "json手动参数格式错误";
            }
        }
        return "true";
    }

    /**
     * 测试依赖数据种，接口名称和接口ID的转换
     */
    public String uriNameToIdFun(String s) throws Throwable {
        org.json.JSONObject value = verification.stringToJsonObject(s);
        org.json.JSONObject obj = new org.json.JSONObject();
        if (value != null) {
            Iterator<String> list = value.keys();
            String key = null;
            while (list.hasNext()) {
                key = list.next();
                if (key.substring(0, 1).equals("/")) {
                    String uriId = uriMapper.getUriIdByUri(key).toString();
                    obj.put(uriId+ "." + key, value.get(key));
                } else if (key.contains("/")) {
                    obj.put(key, value.get(key));
                } else {
                    obj.put(key + "." + uriMapper.getUriByUriId(Integer.parseInt(key)), value.get(key));
                }

            }
        }
        return obj.toString();
    }

    public void uriNameToId(RequestUri uri) throws Throwable {
        if (uri.getUripar() == 2) {
            uri.setUrivar(uriNameToIdFun(uri.getUrivar()));
        }
        uri.setJsontext2(uriNameToIdFun(uri.getJsontext2()));
        uri.setHeadtext2(uriNameToIdFun(uri.getHeadtext2()));
        uri.setFormtext2(uriNameToIdFun(uri.getFormtext2()));
    }

}



