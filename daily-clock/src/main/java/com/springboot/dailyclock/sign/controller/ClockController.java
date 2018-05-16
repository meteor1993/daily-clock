package com.springboot.dailyclock.sign.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.sign.dao.ClockConfigDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.ClockConfigModel;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.sign.model.UserClockLogModel;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.sms.utils.AliyunSMSUtils;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.system.utils.ContextHolderUtils;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 10:02
 **/
@RestController
@RequestMapping(path = "/wechatUser")
public class ClockController {


    private static final Logger logger = LoggerFactory.getLogger(ClockController.class);

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @Autowired
    ClockConfigDao clockConfigDao;

    @Autowired
    UserClockLogDao userClockLogDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
    @PostMapping(value = "/clock")
    public CommonJson clock(@RequestParam String no) throws ParseException {
        CommonJson json = new CommonJson();
        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        // 判断盘口静态变量付值
        String TODAY_NEED_SIGN_USER, TODAY_SIGN_USER_LOG;
        switch (no) {
            case "0":
                TODAY_NEED_SIGN_USER = Constant.TODAY_NEED_SIGN_USER_0;
                TODAY_SIGN_USER_LOG = Constant.TODAY_SIGN_USER_LOG_0;
                break;
            case "1":
                TODAY_NEED_SIGN_USER = Constant.TODAY_NEED_SIGN_USER_1;
                TODAY_SIGN_USER_LOG = Constant.TODAY_SIGN_USER_LOG_1;
                break;
            case "2":
                TODAY_NEED_SIGN_USER = Constant.TODAY_NEED_SIGN_USER_2;
                TODAY_SIGN_USER_LOG = Constant.TODAY_SIGN_USER_LOG_2;
                break;
            case "3":
                TODAY_NEED_SIGN_USER = Constant.TODAY_NEED_SIGN_USER_3;
                TODAY_SIGN_USER_LOG = Constant.TODAY_SIGN_USER_LOG_3;
                break;
            default:
                TODAY_NEED_SIGN_USER = Constant.TODAY_NEED_SIGN_USER_0;
                TODAY_SIGN_USER_LOG = Constant.TODAY_SIGN_USER_LOG_0;
                break;
        }

        boolean hasNeedSign = redisTemplate.hasKey(TODAY_NEED_SIGN_USER);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if (!hasNeedSign) { // 如果需打卡列表不存在
            // 获取当日所有需要打卡列表
            List<NeedClockUserModel> needClockUserModelList = needClockUserDao.findAllByCreateDateBetweenAndNo(simpleDateFormat.parse(sdf.format(new Date()) + " 00:00:00"), simpleDateFormat.parse(sdf.format(new Date()) + " 23:59:59"), no);
            Map<String, Object> map = Maps.newHashMap();
            for (NeedClockUserModel model : needClockUserModelList) {
                map.put(model.getOpenid(), model);
            }
            redisTemplate.opsForHash().putAll(TODAY_NEED_SIGN_USER, map);
            redisTemplate.expire(TODAY_NEED_SIGN_USER, 4, TimeUnit.HOURS); // 设定4小时过期
        }

        if (!redisTemplate.opsForHash().hasKey(TODAY_NEED_SIGN_USER, wxMpUser.getOpenId())) { // 如果当前openid不存在需打卡列表
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("您当前无需打卡");
            return json;
        }

        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs(no);

        Long startTime = simpleDateFormat.parse(sdf.format(new Date()) + clockConfigModel.getClockStartTime()).getTime();
        Long endTime = simpleDateFormat.parse(sdf.format(new Date()) + clockConfigModel.getClockEndTime()).getTime();
        Long nowTime = new Date().getTime();

        if (nowTime < startTime || nowTime > endTime) { // 当前时间不在打卡时间中
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("当前时间不在打卡时间中");
            return json;
        }

        // 已打卡记录
        boolean haskey = redisTemplate.hasKey(TODAY_SIGN_USER_LOG);

        if (haskey) { // 先判断缓存是否存在
            boolean hasSign = redisTemplate.opsForHash().hasKey(TODAY_SIGN_USER_LOG, wxMpUser.getOpenId());
            if (hasSign) { // 判断当前用户是否打卡
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("您今日已打卡，请勿重复打卡");
                return json;
            } else { // 正常打卡流程
                commonSign(wxMpUser.getOpenId(), no, clockConfigModel, TODAY_SIGN_USER_LOG);
            }
        } else {
            commonSign(wxMpUser.getOpenId(), no, clockConfigModel, TODAY_SIGN_USER_LOG);
        }
        return json;
    }

    /**
     * 正常打卡流程
     * 1.先保存用户打卡日志
     * 2.向账户信息中写入最近一次打卡时间 并判断当前用户是否打卡周期结束
     * 3.如果打卡周期结束，更新相关数据
     * @param openid
     * @param no
     * @param clockConfigModel
     * @param TODAY_SIGN_USER_LOG
     */
    private void commonSign(String openid, String no, ClockConfigModel clockConfigModel, String TODAY_SIGN_USER_LOG) {
        UserClockLogModel userClockLogModel = new UserClockLogModel();
        userClockLogModel.setCreateDate(new Date());
        userClockLogModel.setOpenId(openid);
        userClockLogModel.setType(Constant.CLOCK_TYPE_1);
        userClockLogDao.save(userClockLogModel);
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);
        // 向账户信息中写入最近一次打卡时间 并判断当前用户是否打卡周期结束
        DateTime begin;
        DateTime end = new DateTime(new Date());
        Period p;
        switch (no) {
            case "0":
                userAccountModel.setClockDate0(new Date());
                begin = new DateTime(userAccountModel.getOrderDate0());
                p = new Period(begin, end, PeriodType.days());
                if (p.getDays() + 1 == clockConfigModel.getClockTime()) {
                    userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(userAccountModel.getUseBalance0())).toString());
                    userAccountModel.setUseBalance0("");
                    userAccountModel.setOrderDate0(null);
                }
                break;
            case "1":
                userAccountModel.setClockDate1(new Date());
                begin = new DateTime(userAccountModel.getOrderDate1());
                p = new Period(begin, end, PeriodType.days());
                if (p.getDays() + 1 == clockConfigModel.getClockTime()) {
                    userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(userAccountModel.getUseBalance1())).toString());
                    userAccountModel.setUseBalance1("");
                    userAccountModel.setOrderDate1(null);
                }
                break;
            case "2":
                userAccountModel.setClockDate2(new Date());
                begin = new DateTime(userAccountModel.getOrderDate2());
                p = new Period(begin, end, PeriodType.days());
                if (p.getDays() + 1 == clockConfigModel.getClockTime()) {
                    userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(userAccountModel.getUseBalance2())).toString());
                    userAccountModel.setUseBalance2("");
                    userAccountModel.setOrderDate2(null);
                }
                break;
            case "3":
                userAccountModel.setClockDate3(new Date());
                begin = new DateTime(userAccountModel.getOrderDate3());
                p = new Period(begin, end, PeriodType.days());
                if (p.getDays() + 1 == clockConfigModel.getClockTime()) {
                    userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(userAccountModel.getUseBalance3())).toString());
                    userAccountModel.setUseBalance3("");
                    userAccountModel.setOrderDate3(null);
                }
                break;
            default:
                userAccountModel.setClockDate0(new Date());
                begin = new DateTime(userAccountModel.getOrderDate0());
                p = new Period(begin, end, PeriodType.days());
                if (p.getDays() + 1 == clockConfigModel.getClockTime()) {
                    userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(userAccountModel.getUseBalance0())).toString());
                    userAccountModel.setUseBalance0("");
                    userAccountModel.setOrderDate0(null);
                }
                break;
        }
        userAccountDao.save(userAccountModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put(openid, userClockLogModel);
        redisTemplate.opsForHash().putAll(TODAY_SIGN_USER_LOG, map);
        redisTemplate.expire(TODAY_SIGN_USER_LOG, 2, TimeUnit.HOURS); // 设定2小时过期
    }

}
