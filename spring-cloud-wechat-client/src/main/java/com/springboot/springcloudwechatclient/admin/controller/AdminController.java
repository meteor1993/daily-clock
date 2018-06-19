package com.springboot.springcloudwechatclient.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.admin.model.AdminInfoModel;
import com.springboot.springcloudwechatclient.admin.remote.AdminRemote;
import com.springboot.springcloudwechatclient.sign.model.ClockConfigModel;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminRemote adminRemote;

    @Autowired
    SignRemote signRemote;

    @RequestMapping(value = "/adminIndex")
    public String adminIndex() {
        return "admin/adminIndex";
    }

    @RequestMapping(value = "/initAdminInfo")
    @ResponseBody
    public CommonJson initAdminInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CommonJson adminJson = adminRemote.getAdminInfoByCreateDate(simpleDateFormat.format(new Date()));
        return adminJson;
    }

    @RequestMapping(value = "/getAdminInfo")
    @ResponseBody
    public CommonJson getAdminInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String no = ContextHolderUtils.getRequest().getParameter("no");
        String money = ContextHolderUtils.getRequest().getParameter("money");
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>AdminController.getAdminInfo>>>>>>>>>>>>>>>>>>>>");
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>no:" + no + ">>>>>>>money:" + money);
        CommonJson adminJson = adminRemote.getAdminInfoByCreateDate(simpleDateFormat.format(new Date()));
        this.logger.info(">>>>>>>>>>>>>>>>>>>adminRemote.getAdminInfoByCreateDate:" + JSON.toJSONString(adminJson));
        AdminInfoModel adminInfoModel = JSON.parseObject(JSON.toJSONString(adminJson.getResultData().get("adminInfoModel")), AdminInfoModel.class);
        if (adminInfoModel == null) {
            adminInfoModel = new AdminInfoModel();
            adminInfoModel.setCreateDate(new Date());
            adminInfoModel.setNo(no);
        }

        // 汇总数据
        CommonJson dataJson = adminRemote.getDataInfo();
        this.logger.info(">>>>>>>>>>>>>>>>>>>adminRemote.getDataInfo:" + JSON.toJSONString(dataJson));
        // 已打卡总金额
        String clockUserBalance0Sum = dataJson.getResultData().get("clockUserBalance0Sum").toString();
        // 未打卡总金额
        String unClockUserBalance0Sum = dataJson.getResultData().get("unClockUserBalance0Sum").toString();
        // 已打卡总人数
        String clockUserCount0 = dataJson.getResultData().get("clockUserCount0").toString();
        // 未打卡总人数
        String unClockUserCount0 = dataJson.getResultData().get("unClockUserCount0").toString();

        // 盘口配置信息
        CommonJson configJson = signRemote.getClockConfig(no);
        this.logger.info(">>>>>>>>>>>>>>>>>>>signRemote.getClockConfig:" + JSON.toJSONString(configJson));
        ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(configJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);
        // 计算千份奖励
        // 1. 未打卡人数 * 保底金额
        // 2. (未打卡金额 - 保底总额 - 抽水) / 已打卡总金额 * 1000
        // 保底总额
        String baodiSum = new BigDecimal(unClockUserCount0).multiply(new BigDecimal(clockConfigModel.getBaodiAmount())).toString();
        // 待发放金额
        String daiSum = new BigDecimal(unClockUserBalance0Sum).subtract(new BigDecimal(baodiSum)).subtract(new BigDecimal(money)).toString();
        // 千份奖金
        String qianfenAmount = new BigDecimal(daiSum).divide(new BigDecimal(clockUserBalance0Sum), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("1000")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        adminInfoModel.setSumClockUser(clockUserCount0);
        adminInfoModel.setSumClockAmount(clockUserBalance0Sum);
        adminInfoModel.setSumUnClockUser(unClockUserCount0);
        adminInfoModel.setSumUnClockAmount(unClockUserBalance0Sum);

        adminJson = adminRemote.saveAdminInfo(adminInfoModel);
        adminInfoModel = JSON.parseObject(JSON.toJSONString(adminJson.getResultData().get("adminInfoModel")), AdminInfoModel.class);

        Map<String, Object> map = Maps.newHashMap();
        adminInfoModel.setForMeAmount(money);
        adminInfoModel.setQianfenAmount(qianfenAmount);
        map.put("adminInfoModel", adminInfoModel);

        adminJson.setResultData(map);
        adminJson.setResultCode(Constant.JSON_SUCCESS_CODE);
        return adminJson;
    }

    @RequestMapping(value = "/saveAdminInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonJson saveAdminInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String no = ContextHolderUtils.getRequest().getParameter("no");
        String money = ContextHolderUtils.getRequest().getParameter("money");
        String qianfenAmount = ContextHolderUtils.getRequest().getParameter("qianfenAmount");
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>AdminController.getAdminInfo>>>>>>>>>>>>>>>>>>>>");
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>no:" + no + ">>>>>>>money:" + money);
        CommonJson adminJson = adminRemote.getAdminInfoByCreateDate(simpleDateFormat.format(new Date()));
        this.logger.info(">>>>>>>>>>>>>>>>>>>adminRemote.getAdminInfoByCreateDate:" + JSON.toJSONString(adminJson));
        AdminInfoModel adminInfoModel = JSON.parseObject(JSON.toJSONString(adminJson.getResultData().get("adminInfoModel")), AdminInfoModel.class);

        adminInfoModel.setQianfenAmount(qianfenAmount);
        adminInfoModel.setForMeAmount(money);
        adminInfoModel.setUpdateDate(new Date());
        adminJson = adminRemote.saveAdminInfo(adminInfoModel);

        return adminJson;
    }

}
