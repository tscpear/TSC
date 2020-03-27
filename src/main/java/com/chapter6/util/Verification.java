package com.chapter6.util;

import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Iterator;

@Component
public class Verification {
    /**
     * 验证 是否可以转化为 JSONObject
     */
    public boolean isJsonObject(String s){
        if(s==null){
            return false;
        }
        if(s.equals("{}")){
            return true;
        }
        if(s.contains("\n")){
            s = s.replaceAll("\n","");
        }
        try {
            JSONObject obj = new JSONObject(s);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     *把String转JsonObect
     */
    public JSONObject stringToJsonObject(String s) throws Throwable {
        JSONObject obj1 = new JSONObject();
        if("".equals(s)|| s==null){
            return obj1;
        }
        if(isJsonObject(s)){
            s = s.replaceAll("\n","");
            JSONObject obj = new JSONObject(s);
            return obj;

        }else{
            String a = s;
            throw new Throwable("格式错误");
        }
    }
    /**
     * 把一个json放到另一个json里面
     */
    public void objToObj(JSONObject obj1,JSONObject obj2){
        if(obj2!=null){
            Iterator<String> list = obj2.keys();
            while (list.hasNext()){
                String key = list.next();
                obj1.put(key,obj2.get(key));
            }
        }

    }

    /**
     * 验证是不是[{},{}]
     */
    public boolean isJSONArray(String s){
        s = s.replaceAll("\n","");
        try {
            JSONArray array = new JSONArray(s);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * 判断是不是控制
     */
    public boolean isEmpty(Object o){
        if(StringUtils.isEmpty(o)){

            return true;
        }
        if(o==null){
            return true;
        }
        if(o.equals("")){
            return true;
        }
        return false;
    }
}
