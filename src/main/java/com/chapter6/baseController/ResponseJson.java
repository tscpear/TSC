package com.chapter6.baseController;



import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class ResponseJson {
    /**
     * 查询列表成功
     */
    public JSONObject findList(JSONArray data,int count){
        JSONObject response = new JSONObject();
        response.put("data",data);
        response.put("count",count);
        response.put("msg","新增成功");
        response.put("code",0);
        return response;
    }

    /**
     * 更新成功
     */
    public JSONObject updataList(){
        JSONObject response = new JSONObject();
        response.put("msg","编辑成功");
        response.put("code",0);
        return response;
    };
    /**
     * 传递msg
     */
    public JSONObject getMsg(String msg,int code){
        JSONObject response = new JSONObject();
        response.put("msg",msg);
        response.put("code",code);
        response.put("data","");
        return response;
    }
    /**
     *添加成功
     */
    public JSONObject addMsg(int code,String msg){
        JSONObject response = new JSONObject();
        if(code==1){
            response.put("msg",msg);
        }else {
            response.put("msg","");
        }

        response.put("code",code);
        response.put("data","");
        return response;
    }
    /**
     * 请求成功
     */
    public JSONObject getRequest(JSONObject data){
        JSONObject response = new JSONObject();
        response.put("data",data);
        response.put("msg","新增成功");
        response.put("code",0);
        return response;
    }


}
