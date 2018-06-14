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
import com.springboot.dailyclock.sms.dao.SMSDao;
import com.springboot.dailyclock.sms.model.SMSModel;
import com.springboot.dailyclock.sms.utils.AliyunSMSUtils;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.system.utils.ExceptionUtil;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 10:02
 **/
@RestController
@RequestMapping(path = "/api")
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
    SMSDao smsDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发送短信验证码
     * @param mobile
     * @return
     */
    @PostMapping(value = "/sendSMS")
    public CommonJson sendSMS(@RequestParam String sessionId, @RequestParam String mobile) {
        logger.info(">>>>>>>>>sessionId:" + sessionId + ">>>>>>>>>>mobile:" + mobile);
        CommonJson json = new CommonJson();
        if (StringUtils.isEmpty(mobile) || mobile.length() != 11) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("请输入正确的手机号");
            return json;
        }
        // ---------------进入发短信流程--------------
        AliyunSMSUtils smsUtils = AliyunSMSUtils.getInstance();

        String code = getRandomCode();
        logger.info("---------------sendSMS--------------mobile:" + mobile + ",code" + code);
        boolean flag = smsUtils.sendAliyunSMS(mobile, code);
//        boolean flag = true;
        if (flag) {
            SMSModel smsModel = new SMSModel();
            smsModel.setCreateDate(new Date());
            smsModel.setMobile(mobile);
            smsModel.setCreateSessionID(sessionId);
            smsModel.setIsValid(Constant.SUCCESS_CODE);
            smsModel.setVerificationCode(code);
            smsDao.save(smsModel);
        }

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
    public CommonJson binding(@RequestParam String openid, @RequestParam String sessionId, @RequestParam String mobile, @RequestParam String code) {

        logger.info(">>>>>>>>>>>>openid:" + openid +">>>>>>>sessionId:" + sessionId + ">>>>>>>>>>mobile:" + mobile + ">>>>>>>>>>code:" + code);

        CommonJson json = new CommonJson();
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("手机号或验证码为空");
            return json;
        }

        int checkMinutes = 5; // 校验短信有效期

        // 校验短信验证码
        List<SMSModel> smsModelList = smsDao.findAllByCreateSessionIDAndIsValidAndVerificationCodeOrderByCreateDateDesc(sessionId, "1", code);
        try {
            if (smsModelList.size() > 0) { // 查询到相关数据
                SMSModel smsModel = smsModelList.get(0);
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String createDate = simpleFormat.format(smsModel.getCreateDate());
                String nowDate = simpleFormat.format(new Date());
                long create = simpleFormat.parse(createDate).getTime();
                long now = simpleFormat.parse(nowDate).getTime();
                int times = (int) ((now - create)/(1000 * 60));
                if (Constant.SUCCESS_CODE.equals(smsModel.getIsValid())) { // 判断当前验证码是否有效
                    if (times < checkMinutes) { // 判断时间是否小于有效时间
                        // 将验证码变为无效
                        smsModel.setIsValid(Constant.FAIL_CODE);
                        smsDao.save(smsModel);
                        json.setResultCode(Constant.JSON_SUCCESS_CODE);
                        json.setResultMsg("验证码输入正确");
                    } else {
                        json.setResultCode(Constant.JSON_ERROR_CODE);
                        json.setResultMsg("当前验证码已过期，请重新获取");
                        return json;
                    }
                } else {
                    json.setResultCode(Constant.JSON_ERROR_CODE);
                    json.setResultMsg("您的验证码已失效，请重新获取");
                    return json;
                }
            } else {
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("请输入正确的验证码");
                return json;
            }
        } catch (ParseException e) {
            logger.info(ExceptionUtil.getStackMsg(e));
        }

        if (Constant.JSON_SUCCESS_CODE.equals(json.getResultCode())) { // 验证码校验通过
            WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByWechatOpenIdIs(openid);
            if (wechatMpUserModel != null) {
                wechatMpUserModel.setMobile(mobile);
                wechatMpUserDao.save(wechatMpUserModel);
                json.setResultCode(Constant.JSON_SUCCESS_CODE);
                json.setResultMsg("success");
                json.setResultData(null);
            } else {
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("fail");
                json.setResultData(null);
            }

        }

        return json;
    }

    /**
     * 早晨打卡
     * @return
     */
    @PostMapping(value = "/clock")
    public CommonJson clock(@RequestParam WxMpUser wxMpUser, @RequestParam String no) throws ParseException {
        logger.info(">>>>>>>>openid:" + wxMpUser.getOpenId() + ">>>>>>>>>>no:" + no);
        CommonJson json = new CommonJson();
//        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
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
     * 获取盘口配置
     * @return
     */
    @PostMapping(value = "/getClockConfig")
    public CommonJson getClockConfig(@RequestParam String no) {
        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs(no);
        CommonJson json = new CommonJson();

        Map<String, Object> map = Maps.newHashMap();
        map.put("clockConfigModel", clockConfigModel);

        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 盘口0当日打卡情况
     * @return
     */
    @PostMapping(value = "/signInfo0")
    public CommonJson signInfo0() {
        CommonJson json = new CommonJson();
        // 当日已打卡总金额
        String sumClockBalance0 = userAccountDao.getClockUserBalance0Sum(new Date());
        // 当日未打卡总金额
        String unClockUserBalance0Sum = userAccountDao.getUnClockUserBalance0Sum(new Date());
        // 当日打卡总人数
        String clockUserCount = userAccountDao.getClockUserCount0(new Date());
        // 当日未打卡总人数
        String unClockUserCount = userAccountDao.getUnClockUserCount0(new Date());

        Map<String, Object> map = Maps.newHashMap();
        map.put("sumClockBalance0", sumClockBalance0);
        map.put("unClockUserBalance0Sum", unClockUserBalance0Sum);
        map.put("clockUserCount", clockUserCount);
        map.put("unClockUserCount", unClockUserCount);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultData(map);
        json.setResultMsg("成功");
        return json;
    }

    /**
     * 盘口0数据汇总
     * @return
     */
    @PostMapping(value = "/dataAmount")
    public CommonJson dataAmount(@RequestParam String no) {
        CommonJson json = new CommonJson();
        // 盘口0 总金额
        String userBalance0Sum = userAccountDao.getUserBalance0Sum();
        // 盘口0 总账户数
        String userCount0 = userAccountDao.getUserCount0();
        // 盘口0 当日打卡列表
        List<String> openidList = userClockLogDao.findEarlyClockUser(no, new Date());
        WechatMpUserModel wechatMpUserModel = null;
        // 盘口0 每日第一打卡人记录
        UserClockLogModel userClockLogModel = null;
        // 盘口0 当日总需打卡数
        List<String> needClockUser =  needClockUserDao.findAllNeedClockUserModel(new Date());
        if (openidList.size() > 0) {
            wechatMpUserModel = wechatMpUserDao.getByWechatOpenIdIs(openidList.get(0));
            userClockLogModel = userClockLogDao.getByOpenIdAndCreateDate(openidList.get(0), new Date());
            logger.info(">>>>>>>>>>>>>>>date:" + userClockLogModel.getCreateDate().getTime());
        }
        List<String> openidLaterList = userClockLogDao.findLaterClockUser(no, new Date());
        int num = 0;
        List<WechatMpUserModel> wechatMpUserModelList = new ArrayList<>();
        WechatMpUserModel wechatMpUserModel1 = null;
        if (openidLaterList.size() > 9) { // 如果当前打卡人数超过9人，就取十条数据
            num = 10;
        } else { // 如果不大于9人，则有几条取几条
            num = openidLaterList.size();
        }
        for (int i = 0; i < num; i++) {
            wechatMpUserModel1 = wechatMpUserDao.getByWechatOpenIdIs(openidList.get(i));
            wechatMpUserModelList.add(wechatMpUserModel1);
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("userBalance0Sum", userBalance0Sum);
        map.put("userCount0", userCount0);
        map.put("userClockLogModel", userClockLogModel);
        map.put("wechatMpUserModel", wechatMpUserModel);
        map.put("wechatMpUserModelList", wechatMpUserModelList);
        // 盘口0 当日总需打卡数
        map.put("needClockUserSum", needClockUser.size());
        // 盘口0 当日事实打卡人数
        map.put("todayClockUserSum", openidList.size());
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultData(map);
        json.setResultMsg("成功");
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
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);
        UserClockLogModel userClockLogModel = new UserClockLogModel();
        userClockLogModel.setCreateDate(new Date());
        userClockLogModel.setOpenId(openid);
        userClockLogModel.setType(Constant.CLOCK_TYPE_1);
        userClockLogModel.setNo(no);
        userClockLogDao.save(userClockLogModel);

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
                    userAccountModel.setUpdateDate(new Date());
                    userAccountModel.setType0("");
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
                    userAccountModel.setUpdateDate(new Date());
                    userAccountModel.setType1("");
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
                    userAccountModel.setUpdateDate(new Date());
                    userAccountModel.setType2("");
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
                    userAccountModel.setUpdateDate(new Date());
                    userAccountModel.setType3("");
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
                    userAccountModel.setUpdateDate(new Date());
                    userAccountModel.setType0("");
                }
                break;
        }
        userAccountDao.save(userAccountModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put(openid, userClockLogModel);
        redisTemplate.opsForHash().putAll(TODAY_SIGN_USER_LOG, map);
        redisTemplate.expire(TODAY_SIGN_USER_LOG, 2, TimeUnit.HOURS); // 设定2小时过期
    }

    /**
     * 获取随机数
     * @return
     */
    private String getRandomCode() {
        Random rad = new Random();

        String result = rad.nextInt(1000000) + "";

        if(result.length() != 6){
            return getRandomCode();
        }
        return result;
    }

}
