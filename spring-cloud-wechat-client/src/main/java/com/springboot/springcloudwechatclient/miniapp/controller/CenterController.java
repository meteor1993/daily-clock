package com.springboot.springcloudwechatclient.miniapp.controller;

import com.alibaba.fastjson.JSON;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/miniapp/center")
public class CenterController {

    private final Logger logger = LoggerFactory.getLogger(CenterController.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    SignRemote signRemote;

    @PostMapping(value = "/center")
    public CommonJson center() {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>CenterController.center>>>>>>>>>token:" + token + ">>>>>openid:" + openid);

        CommonJson accountJson = accountRemote.accountCenter(openid, "0");
        logger.info(">>>>>>>>>>>>accountRemote.accountCenter:" + JSON.toJSONString(accountJson));

        return accountJson;
    }

    /**
     * 分页查询当前用户进出账记录
     * @return
     */
    @PostMapping(value = "/findUserAccountLogPage")
    public CommonJson findUserAccountLogPage() {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        return accountRemote.findUserAccountLogPage(openid, 0, 10);
    }
}
