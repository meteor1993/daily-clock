package com.springboot.springcloudwechatclient.miniapp.controller;

import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * @program: spring-cloud-wechat-client
 * @description:
 * @author: weishiyao
 * @create: 2018-06-06 23:44
 **/
@RestController
@RequestMapping(value = "/miniapp")
public class TokenController {

    private final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @PostMapping(value = "/getToken")
    public CommonJson getToken() {
        this.logger.info(">>>>>>>>>>>>>>>>>>TokenController.getToken");
        String key = ContextHolderUtils.getRequest().getParameter("key");
        CommonJson json = new CommonJson();
        // 判断key是否相等
        if ("oTuDt4BN7CMec87I5N3k5RMLTzLlmwOc".equals(key)) {
            String token = UUID.randomUUID().toString();

            // 将此token存入缓存，token为key


            Map<String, Object> map = Maps.newHashMap();
            map.put("token", token);
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultData(map);
            json.setResultMsg("success");
        } else {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("error key");
        }
        return json;
    }
}
