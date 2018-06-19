package com.springboot.springcloudwechatclient.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.admin.remote.AdminRemote;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/adminClock")
public class AdminClockController {

    private final Logger logger = LoggerFactory.getLogger(AdminClockController.class);

    @Autowired
    AdminRemote adminRemote;

    @RequestMapping(value = "/index")
    public String index() {
        return "admin/adminClock";
    }


    /**
     * 根据手机号获取用户信息
     */
    @PostMapping(value = "/getUserByMobile")
    @ResponseBody
    public CommonJson getUserByMobile(@RequestParam String mobile) {
        System.out.println("AdminClockController.getUserByMobile");
        CommonJson json = adminRemote.getUserByMobile(mobile);
        this.logger.info(">>>>>>>AdminClockController.getUserByMobile>>>>>>>" + JSON.toJSONString(json));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("userAccountModel")), UserAccountModel.class);
        WechatMpUserModel wechatMpUserModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
        Map<String, Object> map = Maps.newHashMap();
        map.put("type", userAccountModel.getType0());
        map.put("useBalance0", userAccountModel.getUseBalance0());
        map.put("clockDate", userAccountModel.getClockDate0());
        map.put("orderDate", userAccountModel.getOrderDate0());
        map.put("continuousClockNum", userAccountModel.getContinuousClockNum());
        map.put("mobile", wechatMpUserModel.getMobile());
        map.put("openid", wechatMpUserModel.getWechatOpenId());
        map.put("name", wechatMpUserModel.getWechatNickName());
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }
}
