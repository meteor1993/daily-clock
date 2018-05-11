package com.springboot.dailyclock.system.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by weishiyao on 2018/1/14.
 * ajaxjson工具类
 */
public class AjaxJson {
    private boolean success = true;// 是否成功
    private String msg = "操作成功";// 提示信息
    private Object obj = null;// 其他信息
    private Map<String, Object> attributes;// 其他参数
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getJsonStr(){
        JSONObject obj = new JSONObject();
        obj.put("success", this.isSuccess());
        obj.put("msg", this.getMsg());
        obj.put("obj", this.obj);
        obj.put("attributes", this.attributes);
        return obj.toJSONString();
    }
}
