package com.springboot.springcloudwechatclient.account.controller;

import com.alibaba.fastjson.JSON;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by weisy on 2018/5/22
 */
@Controller
@RequestMapping(value = "/wxMp/account")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    AccountRemote accountRemote;

    @RequestMapping(value = "/center")
    public String center() {
//        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        WxMpUser wxMpUser = new WxMpUser();
        wxMpUser.setOpenId("111111");
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.accountInfo, openid:" + wxMpUser.getOpenId());
        CommonJson json = accountRemote.accountInfo(wxMpUser.getOpenId());
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.accountInfo:" + JSON.toJSONString(json));
        ContextHolderUtils.getRequest().setAttribute("userAccountModel", json.getResultData().get("userAccountModel"));
        return "wxmp/sign/center";
    }
}
