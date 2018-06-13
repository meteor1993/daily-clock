package com.springboot.springcloudwechatclient.pay.controller;

import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/miniPayNotify")
public class WxMaPayNotifyController {

    private final Logger logger = LoggerFactory.getLogger(WxMaPayNotifyController.class);

    /**
     * 微信支付通知请求，被微信调用
     * @param request
     * @return
     */
    @PostMapping(value = "/notify")
    public CommonJson notify(HttpServletRequest request) {
        CommonJson json = new CommonJson();

        return json;
    }
}
