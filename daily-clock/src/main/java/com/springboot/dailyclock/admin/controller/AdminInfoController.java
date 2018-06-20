package com.springboot.dailyclock.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.dao.UserAccountLogDao;
import com.springboot.dailyclock.account.model.UserAccountLogModel;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.admin.dao.AdminInfoDao;
import com.springboot.dailyclock.admin.model.AdminInfoModel;
import com.springboot.dailyclock.admin.service.AdminService;
import com.springboot.dailyclock.sign.dao.ClockConfigDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.ClockConfigModel;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by weisy on 2018/5/22
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminInfoController {

    private final Logger logger = LoggerFactory.getLogger(AdminInfoController.class);

    @Autowired
    AdminInfoDao adminInfoDao;

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    UserAccountLogDao userAccountLogDao;

    @Autowired
    ClockConfigDao clockConfigDao;

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    @Autowired
    AdminService adminService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 根据创建日期得到AdminInfo
     * @param date
     * @return
     */
    @PostMapping(value = "/getAdminInfoByCreateDate")
    public CommonJson getAdminInfoByCreateDate(@RequestParam String date) {
        this.logger.info(">>>>>>>>>>>>>>>>>>>>AdminInfoController.getAdminInfoByCreateDate>>>>>>>>>>>>>>>>>");
        CommonJson json = new CommonJson();
        AdminInfoModel adminInfoModel = adminInfoDao.getAdminInfoModelByCreateDate(date);
        Map<String, Object> map = Maps.newHashMap();
        map.put("adminInfoModel", adminInfoModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 保存/更新 adminInfoModel
     * @param adminInfoModel
     * @return
     */
    @PostMapping(value = "/saveAdminInfo")
    public CommonJson saveAdminInfo(@RequestBody AdminInfoModel adminInfoModel) {
        this.logger.info(">>>>>>>>>>>>>>>>>>>>AdminInfoController.saveAdminInfo>>>>>>>>>>>>>>>>>");
        CommonJson json = new CommonJson();
        AdminInfoModel adminInfoModel1 = adminInfoDao.save(adminInfoModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put("adminInfoModel", adminInfoModel1);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 管理员数据查询
     * @return
     */
    @PostMapping(value = "/getDataInfo")
    public CommonJson getDataInfo() {
        CommonJson json = new CommonJson();
        // 已打卡总金额
        String clockUserBalance0Sum = userAccountDao.getClockUserBalance0Sum(new Date());
        // 未打卡总金额
        String unClockUserBalance0Sum = userAccountDao.getUnClockUserBalance0Sum(new Date());
        // 已打卡总人数
        String clockUserCount0 = userAccountDao.getClockUserCount0(new Date());
        // 未打卡总人数
        String unClockUserCount0 = userAccountDao.getUnClockUserCount0(new Date());
        Map<String, Object> map = Maps.newHashMap();
        map.put("clockUserBalance0Sum", clockUserBalance0Sum);
        map.put("unClockUserBalance0Sum", unClockUserBalance0Sum);
        map.put("clockUserCount0", clockUserCount0);
        map.put("unClockUserCount0", unClockUserCount0);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 根据手机号查询账户信息
     * @param mobile
     * @return
     */
    @PostMapping(value = "/getUserByMobile")
    public CommonJson getUserByMobile(@RequestParam String mobile) {
        CommonJson json = new CommonJson();
        WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByMobileIs(mobile);
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(wechatMpUserModel.getWechatOpenId());
        Map<String, Object> map = Maps.newHashMap();
        map.put("wechatMpUserModel", wechatMpUserModel);
        map.put("userAccountModel", userAccountModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 每日数据结算
     * @return
     * @throws ParseException
     */
    @PostMapping(value = "/gatherData")
    public CommonJson gatherData() {
        CommonJson json = new CommonJson();
        try {
            json = adminService.gatherData();
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("success");
        } catch (ParseException e) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("fail");
        }

        return json;
    }
}
