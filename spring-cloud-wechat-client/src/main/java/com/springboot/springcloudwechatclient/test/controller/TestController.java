package com.springboot.springcloudwechatclient.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/test")
    public String test() {
        logger.info(">>>>>>>>>>>>>>>>TestController.test>>>>>>>>>>>>>>>>>>>>>");
        return "wxmp/sign/center";
    }
}
