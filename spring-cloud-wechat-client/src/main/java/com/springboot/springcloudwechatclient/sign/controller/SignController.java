package com.springboot.springcloudwechatclient.sign.controller;

import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.sign.remote.WechatMpUserRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: spring-cloud-wechat-client
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 21:35
 **/
@Controller
@RequestMapping(value = "/wxMp")
public class SignController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    WechatMpUserRemote wechatMpUserRemote;

    @RequestMapping(value = "/index")
    public String index() {
        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        CommonJson json = wechatMpUserRemote.getWechatMpUserByOpenid(wxMpUser.getOpenId());
        WechatMpUserModel wechatMpUserModel = (WechatMpUserModel) json.getResultData().get("wechatMpUserModel");
        // 判断当前用户是否绑定
        if (wechatMpUserModel != null) { // 已绑定
            return "wxmp/sign/index";
        } else { // 未绑定
            return  "wxmp/sign/binding";
        }
    }
}
