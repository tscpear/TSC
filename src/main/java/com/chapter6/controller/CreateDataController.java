package com.chapter6.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chapter6.baseController.ResponseJson;
import com.chapter6.model.Ss;
import com.chapter6.model.request.CreateData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("createData")
public class CreateDataController {

    ResponseJson responseJson = new ResponseJson();

    @PostMapping("byOrderSn")
    @ResponseBody
    public JSONObject deleteTestCase(CreateData createData) {
        if(createData.getOrderType()==1){
            return responseJson.getMsg("门店网红订单");
        }
        if(createData.getOrderType()==2){
            return  responseJson.getMsg("司机网红订单");
        }
        return responseJson.getMsg("哦好急哦");
    }
}
