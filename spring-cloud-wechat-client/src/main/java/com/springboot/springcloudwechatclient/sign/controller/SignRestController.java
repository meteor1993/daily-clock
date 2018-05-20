package com.springboot.springcloudwechatclient.sign.controller;

import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @program: spring-cloud-wechat-client
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 19:02
 **/
@RestController
@RequestMapping(path = "/signRestApi")
public class SignRestController {

    private final static Logger logger = LoggerFactory.getLogger(SignRestController.class);

    @Autowired
    SignRemote signRemote;

    @PostMapping(value = "/sendSMS")
    @ResponseBody
    public CommonJson sendSMS(@RequestParam String mobile) {
        logger.info(">>>>>>>>>>>>>sessionId:" + ContextHolderUtils.getSession().getId() + ">>>>>>>>>>>>>>>>mobile:" + mobile);
        return signRemote.sendSMS(ContextHolderUtils.getSession().getId(), mobile);
    }

    @PostMapping(value = "/binding")
    @ResponseBody
    public CommonJson binding(@RequestParam String mobile, @RequestParam String code) {
        logger.info(">>>>>>>>>>>>>mobile:" + mobile + ">>>>>>>>>>>>>>>>code:" + code);
        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        String sessionId = ContextHolderUtils.getSession().getId();
        return signRemote.binding(wxMpUser, sessionId, mobile, code);
    }

    @PostMapping(value = "/clock")
    @ResponseBody
    public CommonJson clock(@RequestParam String no) {
        logger.info(">>>>>>>>>>>>>no:" + no);
        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        return signRemote.clock(wxMpUser, no);
    }

}
