package com.chapter6.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chapter6.baseController.ResponseJson;
import com.chapter6.mapper.TestMapper;
import com.chapter6.mapper.UriMapper;
import com.chapter6.model.request.ListParam;
import com.chapter6.model.request.RequestTestCase;
import com.chapter6.model.request.RequestUri;
import com.chapter6.model.response.ResponseTestCase;
import com.chapter6.util.Verification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("test")
public class TestController {


    @Autowired
    private TestMapper testMapper;
    @Autowired
    private UriMapper uriMapper;
    @Autowired
    private ResponseJson responseJson;
    @Autowired
    private Verification verification;


    @GetMapping("/list")
    @ResponseBody
    public JSONObject testList(@RequestParam int page,
                               @RequestParam int limit,
                               @RequestParam(required = false) String uriValue,
                               @RequestParam(required = false) String testMark,
                               @RequestParam(required = false) Integer   device) {
        JSONArray array = new JSONArray();
        ListParam param = new ListParam();
        param.setUriValue(uriValue);
        param.setPageBegin(page * limit - limit);
        param.setPageEnd(page * limit);
        param.setTestMark(testMark);
        param.setDevice(device);
        List<RequestTestCase> requestTestCaseList = testMapper.testList(param);

        for (RequestTestCase requestTestCase : requestTestCaseList) {
            ResponseTestCase responseTestCase = new ResponseTestCase();

            RequestUri requestUri = uriMapper.getUriById(requestTestCase.getUriId());
            BeanUtils.copyProperties(requestTestCase, responseTestCase);
            responseTestCase.setMethod(requestTestCase.getMethod1() + requestTestCase.getMethod2() + requestTestCase.getMethod3());
            responseTestCase.setUri(requestUri.getUri());
            responseTestCase.setDevice(requestUri.getDevice());


            if (!StringUtils.isEmpty(requestTestCase.getRely())) {
                String[] arr = requestTestCase.getRely().split(",");
                JSONObject byrely = new JSONObject();
                for (int i = 0; i < arr.length; i++) {
                    //通过依赖的测试用例查询 用例对应的接口id
                    int uriId = testMapper.getUriIdById(Integer.parseInt(arr[i]));
                    //通过uriId 获取测试用例list
                    List<RequestTestCase> testCaseList = testMapper.findTestCaseByUriId(uriId);
                    JSONArray testCaseArray = new JSONArray();
                    for (int j = 0; j < testCaseList.size(); j++) {
                        testCaseArray.add(testCaseList.get(j).getTestCaseId());
                    }
                    //通过uriId合成依赖接口
                    String uriKey = uriId + "." + uriMapper.getUriByUriId(uriId) + "." + arr[i];
                    byrely.put(uriKey, testCaseArray);
                }
                responseTestCase.setByRely(byrely.toString());
            }


            //账号

            array.add(responseTestCase);


        }
        Integer count = testMapper.getCountList(param);
        return responseJson.findList(array, count);
    }

    @PostMapping("add")
    @ResponseBody
    public JSONObject addTestCase(RequestTestCase requestTestCase) {
        RequestUri requestUri = uriMapper.getUriById(requestTestCase.getUriId());
        if (isRequestTestCaseValueTrue(requestTestCase, requestUri).equals("true")) {
            String rely = requestTestCase.getRely();
            if (rely.length() > 0) {
                if (rely.substring(rely.length() - 1, rely.length()).equals(",")) {
                    requestTestCase.setRely(rely.substring(0, rely.length() - 1));
                }
            }
            testMapper.addTestCase(requestTestCase);
            return responseJson.addMsg(0, "新增成功");
        } else {
            return responseJson.addMsg(1, isRequestTestCaseValueTrue(requestTestCase, requestUri));
        }
    }

    @PostMapping("del")
    @ResponseBody
    public JSONObject deleteTestCase(int testCaseId) {
        testMapper.delTestCase(testCaseId);
        return responseJson.getMsg("删除成功",0);
    }

    @PostMapping("update")
    @ResponseBody
    public JSONObject update(RequestTestCase requestTestCase) {
        RequestUri requestUri = uriMapper.getUriById(requestTestCase.getUriId());
        if (isRequestTestCaseValueTrue(requestTestCase, requestUri).equals("true")) {
            String rely = requestTestCase.getRely();
            if (rely.length() > 0) {
                if (rely.substring(rely.length() - 1, rely.length()).equals(",")) {
                    requestTestCase.setRely(rely.substring(0, rely.length() - 1));
                }
            }
            testMapper.updateTestCase(requestTestCase);
            return responseJson.addMsg(0, "编辑成功");
        } else {
            return responseJson.addMsg(1, isRequestTestCaseValueTrue(requestTestCase, requestUri));
        }
    }


    public String isRequestTestCaseValueTrue(RequestTestCase requestTestCase, RequestUri requestUri) {
        if (requestTestCase.getMethod1() != 1 && requestTestCase.getMethod2() != 2 && requestTestCase.getMethod3() != 3) {
            return "用例类型不能为空";
        }
        if (requestUri.getHead3() == 3) {
            if (!verification.isJsonObject(requestTestCase.getHeadvar())) {
                return "头手动变量格式错误";
            }
        }
        if (requestUri.getWebform3() == 3) {
            if (!verification.isJsonObject(requestTestCase.getWebform())) {
                return "webform手动变量格式错误";
            }
        }
        if (requestUri.getJson3() == 3) {
            if (!verification.isJsonObject(requestTestCase.getJson())) {
                return "json手动变量格式错误";
            }
        }
        return "true";
    }
}
