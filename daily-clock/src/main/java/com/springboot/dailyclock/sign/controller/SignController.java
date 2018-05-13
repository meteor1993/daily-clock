package com.springboot.dailyclock.sign.controller;

import com.springboot.dailyclock.sign.dao.SignConfigDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.UserClockLogModel;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.sms.utils.AliyunSMSUtils;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.system.utils.ContextHolderUtils;
import com.springboot.dailyclock.system.utils.RedisUtil;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 10:02
 **/
@RestController
@RequestMapping(path = "/wechatUser")
public class SignController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @Autowired
    SignConfigDao signConfigDao;

    @Autowired
    UserClockLogDao userClockLogDao;

    /**
     * 发送短信验证码
     * @param mobile
     * @return
     */
    @PostMapping(value = "/sendSMS")
    public CommonJson sendSMS(@RequestParam String mobile) {
        CommonJson json = new CommonJson();
        if (StringUtils.isEmpty(mobile) || mobile.length() != 11) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("请输入正确的手机号");
            return json;
        }
        // ---------------进入发短信流程--------------
        AliyunSMSUtils smsUtils = AliyunSMSUtils.getInstance();

        boolean flag = smsUtils.sendSMS(mobile);

        if (flag) {
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("短信发送成功");
        } else {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("网络繁忙，请稍后从试");
        }

        return json;
    }

    /**
     * 用户绑定
     * @param mobile
     * @param code
     * @return
     */
    @PostMapping(value = "/binding")
    public CommonJson binding(@RequestParam String mobile, @RequestParam String code) {
        CommonJson json = new CommonJson();
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("手机号或验证码为空");
            return json;
        }
        AliyunSMSUtils smsUtils = AliyunSMSUtils.getInstance();

        int checkMinutes = 5; // 校验短信有效期

        json = smsUtils.checkVerificationCode(code, checkMinutes);

        if (Constant.JSON_SUCCESS_CODE.equals(json.getResultCode())) { // 验证码校验通过
            WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
            if (StringUtils.isEmpty(wxMpUser.getOpenId())) { //判断当前是否存在openid
                WechatMpUserModel wechatMpUserModel = new WechatMpUserModel();
                wechatMpUserModel.setMobile(mobile);
                wechatMpUserModel.setCreateDate(new Date());
                wechatMpUserModel.setWechatOpenId(wxMpUser.getOpenId());
                wechatMpUserModel.setWechatUnionId(wxMpUser.getUnionId());
                wechatMpUserModel.setWechatCity(wxMpUser.getCity());
                wechatMpUserModel.setWechatCountry(wxMpUser.getCountry());
                wechatMpUserModel.setWechatHeadImgUrl(wxMpUser.getHeadImgUrl());
                wechatMpUserModel.setWechatLanguage(wxMpUser.getLanguage());
                wechatMpUserModel.setWechatNickName(wxMpUser.getNickname());
                wechatMpUserModel.setWechatProvince(wxMpUser.getProvince());
                wechatMpUserModel.setWechatSex(wxMpUser.getSex());
                wechatMpUserDao.save(wechatMpUserModel);
                json.setResultCode(Constant.JSON_SUCCESS_CODE);
                json.setResultMsg("绑定成功");
            }
        }

        return json;
    }

    /**
     * 早晨打卡
     * @return
     */
    @PostMapping(value = "/sign")
    public CommonJson sign() {
        CommonJson json = new CommonJson();

        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);

        RedisUtil redisUtil = RedisUtil.getInstance();

        boolean haskey = redisUtil.hasKey(Constant.TODAY_SIGN_USER);

        if (haskey) { // 先判断缓存是否存在

            Map<Object, Object> signMap = redisUtil.hmget(Constant.TODAY_SIGN_USER);

            if (signMap.containsKey(wxMpUser.getOpenId())) { // 判断当前用户是否打卡
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("您今日已签到，请勿重复打卡");
                return json;
            } else {
                UserClockLogModel userClockLogModel = new UserClockLogModel();
                userClockLogModel.setCreateDate(new Date());
                userClockLogModel.setOpenId(wxMpUser.getOpenId());
                userClockLogModel.setType(Constant.CLOCK_TYPE_1);
                userClockLogDao.save(userClockLogModel);
                signMap.put(wxMpUser.getOpenId(), userClockLogModel);
//                redisUtil.hmset(Constant.TODAY_SIGN_USER, signMap, 1000 * 60 * 60 * 2);
            }
        }
        return json;
    }

}
