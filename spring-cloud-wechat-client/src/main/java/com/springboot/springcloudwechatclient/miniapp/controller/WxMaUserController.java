package com.springboot.springcloudwechatclient.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: spring-cloud-wechat-client
 * @description: 小程序用户接口
 * @author: weishiyao
 * @create: 2018-06-05 22:37
 **/
@RestController
@RequestMapping(value = "/miniapp/user")
public class WxMaUserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMaService wxService;

    /**
     * 登陆接口
     */
    @PostMapping("/login")
    public CommonJson login(@RequestParam String code) {
        this.logger.info(">>>>>>>>>>>>code:" + code);
        CommonJson json = new CommonJson();
        if (StringUtils.isBlank(code)) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("empty jscode");
            return json;
        }

        try {
            WxMaJscode2SessionResult session = this.wxService.getUserService().getSessionInfo(code);
            this.logger.info(">>>>>>>SessionKey:" + session.getSessionKey() + ",>>>>>>>>openId:" + session.getOpenid() + ",>>>>>unionId:" + session.getUnionid());

            ContextHolderUtils.getSession().setAttribute(Constant.WX_MINIAPP_OPENID, session.getOpenid());

            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            Map<String, Object> map = Maps.newHashMap();
            map.put("session", session);
            json.setResultData(map);
            json.setResultMsg("success");
            return json;
        } catch (WxErrorException e) {
            this.logger.error(e.getMessage(), e);
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg(e.getMessage());
            return json;
        }
    }

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @PostMapping("/info")
    public CommonJson info(@RequestParam String sessionKey, @RequestParam String signature, @RequestParam String rawData, @RequestParam String encryptedData, @RequestParam String iv) {
        this.logger.info(">>>>>>>>>>>>sessionKey:" + sessionKey + ", signature:" + signature + ", rawData:" + rawData + ", encryptedData"+ encryptedData + ", iv:" + iv);
        CommonJson json = new CommonJson();
        // 用户信息校验
        if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("user check failed");
            return json;
        }

        // 解密用户信息
        WxMaUserInfo userInfo = this.wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userInfo", userInfo);
        json.setResultData(map);
        json.setResultMsg("success");
        return json;
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @PostMapping("/phone")
    public CommonJson phone(@RequestParam String sessionKey, @RequestParam String signature, @RequestParam String rawData, @RequestParam String encryptedData, @RequestParam String iv) {
        this.logger.info(">>>>>>>>>>>>sessionKey:" + sessionKey + ", signature:" + signature + ", rawData:" + rawData + ", encryptedData"+ encryptedData + ", iv:" + iv);
        CommonJson json = new CommonJson();
        // 用户信息校验
        if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("user check failed");
            return json;
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = this.wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        Map<String, Object> map = Maps.newHashMap();
        map.put("phoneNoInfo", phoneNoInfo);
        json.setResultData(map);
        json.setResultMsg("success");
        return json;
    }
}
