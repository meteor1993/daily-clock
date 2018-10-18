package com.springboot.dailyclock.sign.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    UserAccountDao userAccountDao;

    @PostMapping(value = "/getWechatMpUserByOpenid")
    public CommonJson getWechatMpUserByOpenid(@RequestParam String openid) {
        logger.info(">>>>>>>>>>>>openid:" + openid);
        CommonJson json = new CommonJson();
        WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByWechatOpenIdIs(openid);
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);
        Map<String, Object> map = Maps.newHashMap();
        map.put("wechatMpUserModel", wechatMpUserModel);
        map.put("userAccountModel", userAccountModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultData(map);
        json.setResultMsg("成功");
        return json;
    }

    @PostMapping(value = "/saveWechatMpUser")
    public CommonJson saveWechatMpUser(@RequestBody WechatMpUserModel wechatMpUserModel) {
        CommonJson json = new CommonJson();
        WechatMpUserModel wechatMpUserModel1 = wechatMpUserDao.save(wechatMpUserModel);

        Map<String, Object> map = Maps.newHashMap();
        map.put("wechatMpUserModel", wechatMpUserModel1);

        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);

        return json;
    }

    /**
     * 根据unionid获取账户信息
     * @param unionId
     * @return
     */
    @PostMapping(value = "/getUserAccountByUnionId")
    public CommonJson getUserAccountByUnionId(@RequestParam String unionId) {
        logger.info("WechatMpUserController.getUserAccountByUnionId>>>>>>>>>>>>>unionId:" + unionId);
        CommonJson json = new CommonJson();
        WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByWechatUnionId(unionId);
        Map<String, Object> map = Maps.newHashMap();
        if (wechatMpUserModel != null) { // 如果绑定信息存在
            UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(wechatMpUserModel.getWechatOpenId());
            if (userAccountModel != null) { // 如果账户信息存在
                map.put("userAccountModel", userAccountModel);
            }
        }
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }
}
