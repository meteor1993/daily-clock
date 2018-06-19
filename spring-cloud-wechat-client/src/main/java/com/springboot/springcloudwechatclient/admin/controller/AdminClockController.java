package com.springboot.springcloudwechatclient.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.admin.remote.AdminRemote;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
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

    @Autowired
    SignRemote signRemote;


}
