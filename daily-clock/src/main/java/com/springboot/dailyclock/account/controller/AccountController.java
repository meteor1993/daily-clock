package com.springboot.dailyclock.account.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
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
 * @create: 2018-05-17 20:04
 **/
@RestController
@RequestMapping(path = "/account")
public class AccountController {

    @Autowired
    UserAccountDao userAccountDao;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @PostMapping(value = "/info")
    public CommonJson info(@RequestParam String openid) {
        CommonJson json = new CommonJson();
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userAccountModel", userAccountModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultData(map);
        json.setResultMsg("成功");
        return json;
    }
}
