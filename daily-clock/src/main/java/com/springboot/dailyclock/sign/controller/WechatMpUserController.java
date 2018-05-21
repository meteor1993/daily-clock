package com.springboot.dailyclock.sign.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 21:38
 **/
@RestController
@RequestMapping(path = "/api")
public class WechatMpUserController {

    private static final Logger logger = LoggerFactory.getLogger(WechatMpUserController.class);

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @PostMapping(value = "/getWechatMpUserByOpenid")
    public CommonJson getWechatMpUserByOpenid(@RequestParam String openid) {
        logger.info(">>>>>>>>>>>>openid:" + openid);
        CommonJson json = new CommonJson();
        WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByWechatOpenIdIs(openid);
        Map<String, Object> map = Maps.newHashMap();
        map.put("wechatMpUserModel", wechatMpUserModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultData(map);
        json.setResultMsg("成功");
        return json;
    }
}
