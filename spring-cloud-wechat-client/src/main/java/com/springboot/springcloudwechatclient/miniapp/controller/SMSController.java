package com.springboot.springcloudwechatclient.miniapp.controller;

import com.alibaba.fastjson.JSON;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/miniapp/sms")
public class SMSController {

    private final Logger logger = LoggerFactory.getLogger(SMSController.class);

    @Autowired
    SignRemote signRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping(value = "/sendSMS")
    public CommonJson sendSMS(@RequestParam String mobile) {
        System.out.println("SMSController.sendSMS");
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>>>>>>>SMSController.sendSMS>>>>>>>>>>token:" + token + ">>>>>>>>>>>>>openid:" + openid + ">>>>>>>>>>>>mobile:" + mobile);
        return signRemote.sendSMS(token, mobile);
    }

    @PostMapping(value = "/binding")
    public CommonJson binding(@RequestParam String mobile, @RequestParam String code) {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>>>>>>>SMSController.binding>>>>>>>>>>token:" + token + ">>>>>>>>>>>>>openid:" + openid + ">>>>>>>>>>>>mobile:" + mobile + ">>>>>>>>code:" + code);
        CommonJson json = signRemote.binding(openid, token, mobile, code);
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>SMSController.binding:" + JSON.toJSONString(json));
        return json;
    }
}
