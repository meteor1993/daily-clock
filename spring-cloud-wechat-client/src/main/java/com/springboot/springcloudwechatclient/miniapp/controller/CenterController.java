package com.springboot.springcloudwechatclient.miniapp.controller;

import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/miniapp/center")
public class CenterController {

    private final Logger logger = LoggerFactory.getLogger(CenterController.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    AccountRemote accountRemote;

    @PostMapping(value = "/center")
    public CommonJson center() {
        System.out.println("CenterController.center");
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>CenterController.center>>>>>>>>>token:" + token + ">>>>>openid:" + openid);




        CommonJson json = new CommonJson();

        return json;
    }
}
