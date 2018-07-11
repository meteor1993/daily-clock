package com.springboot.springcloudwechatclient.admin.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.admin.model.AdminInfoModel;
import com.springboot.springcloudwechatclient.admin.remote.AdminRemote;
import com.springboot.springcloudwechatclient.admin.remote.GatherDataRemote;
import com.springboot.springcloudwechatclient.pay.model.WechatEntPayModel;
import com.springboot.springcloudwechatclient.pay.remote.PayRemote;
import com.springboot.springcloudwechatclient.sign.model.ClockConfigModel;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
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
@RequestMapping(value = "/wxMp/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminRemote adminRemote;

    @Autowired
    SignRemote signRemote;

    @Autowired
    PayRemote payRemote;

    @Autowired
    GatherDataRemote gatherDataRemote;

    @Autowired
    WxPayService wxPayService;

    /**
     * 进入每日统计页面
     * @return
     */
    @RequestMapping(value = "/adminIndex")
    public String adminIndex() {
        return "admin/adminIndex";
    }

    /**
     * 初始化每日统计数据
     * @return
     */
    @RequestMapping(value = "/initAdminInfo")
    @ResponseBody
    public CommonJson initAdminInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CommonJson adminJson = adminRemote.getAdminInfoByCreateDate(simpleDateFormat.format(new Date()));
        return adminJson;
    }

    /**
     * 管理员获取当日统计数据
     * @return
     */
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
        String daiSum = new BigDecimal(unClockUserBalance0Sum).subtract(new BigDecimal(baodiSum)).subtract(new BigDecimal(money)).add(new BigDecimal(clockConfigModel.getSubsidy())).toString();
        // 千份奖金
        String qianfenAmount = new BigDecimal(daiSum).divide(new BigDecimal(clockUserBalance0Sum), 8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("1000")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        adminInfoModel.setSumClockUser(clockUserCount0);
        adminInfoModel.setSumClockAmount(clockUserBalance0Sum);
        adminInfoModel.setSumUnClockUser(unClockUserCount0);
        adminInfoModel.setSumUnClockAmount(unClockUserBalance0Sum);
        adminInfoModel.setSubsidy(clockConfigModel.getSubsidy());

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

    /**
     * 保存统计数据
     * @return
     */
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

    /**
     * 进入补打卡页面
     * @return
     */
    @RequestMapping(value = "/adminClock")
    public String index() {
        return "admin/adminClock";
    }


    /**
     * 根据手机号获取用户信息
     */
    @PostMapping(value = "/getUserByMobile")
    @ResponseBody
    public CommonJson getUserByMobile(@RequestParam String mobile) {
        System.out.println("AdminClockController.getUserByMobile");
        CommonJson json = adminRemote.getUserByMobile(mobile);
        this.logger.info(">>>>>>>AdminClockController.getUserByMobile>>>>>>>" + JSON.toJSONString(json));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("userAccountModel")), UserAccountModel.class);
        WechatMpUserModel wechatMpUserModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
        Map<String, Object> map = Maps.newHashMap();
        map.put("type", userAccountModel.getType0());
        map.put("useBalance0", userAccountModel.getUseBalance0());
        map.put("clockDate", userAccountModel.getClockDate0());
        map.put("orderDate", userAccountModel.getOrderDate0());
        map.put("continuousClockNum", userAccountModel.getContinuousClockNum());
        map.put("mobile", wechatMpUserModel.getMobile());
        map.put("openid", wechatMpUserModel.getWechatOpenId());
        map.put("name", wechatMpUserModel.getWechatNickName());
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 管理员补打卡
     * @param openid
     * @return
     */
    @PostMapping(value = "/helpUserClock")
    @ResponseBody
    public CommonJson helpUserClock(@RequestParam String openid) {
        CommonJson json = signRemote.clock(openid, "0", Constant.CLOCK_TYPE_2);
        json.setResultData(null);
        return json;
    }

    @PostMapping(value = "/gatherData")
    @ResponseBody
    public CommonJson gatherData() {
        CommonJson json = gatherDataRemote.gatherData();
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        return json;
    }

    /**
     * 提现审核页面
     * @return
     */
    @RequestMapping(value = "/entIndex")
    public String entIndex() {
        return "admin/entIndex";
    }

    /**
     * 根据状态查询企业付款列表
     * @return
     */
    @PostMapping(value = "/findEntPayListByStatus")
    @ResponseBody
    public CommonJson findEntPayListByStatus() {
        return payRemote.findEntPayListByStatus("2");
    }

    @PostMapping(value = "/entPay")
    @ResponseBody

    public CommonJson entPay(@RequestParam String id) {
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>AdminController.entPay>>>>>>>>>>>>>>>>>>>>id:" + id);
        CommonJson payJson = payRemote.getEntPapById(id);
        WechatEntPayModel wechatEntPayModel = JSON.parseObject(JSON.toJSONString(payJson.getResultData().get("model")), WechatEntPayModel.class);
        this.logger.info(">>>>>>>>>>>>>>>>>>>>>>payRemote.getEntPapById>>>>>>>>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(payJson.getResultData()));

        CommonJson json = new CommonJson();

        EntPayRequest entPayRequest = EntPayRequest.newBuilder().build();
        entPayRequest.setNonceStr(wechatEntPayModel.getNonce_str());
        // 设备号，非必填
        entPayRequest.setDeviceInfo("");
        // 订单号
        entPayRequest.setPartnerTradeNo(wechatEntPayModel.getPartner_trade_no());
        // openid
        entPayRequest.setOpenid(wechatEntPayModel.getOpenid());
        // 校验用户姓名选项 NO_CHECK：不校验真实姓名  FORCE_CHECK：强校验真实姓名
        entPayRequest.setCheckName(wechatEntPayModel.getCheck_name());
        // 金额
        entPayRequest.setAmount(wechatEntPayModel.getAmount());
        // 企业付款描述信息
        entPayRequest.setDescription(wechatEntPayModel.getDesc());
        // Ip地址
        entPayRequest.setSpbillCreateIp(wechatEntPayModel.getSpbill_create_ip());

        try {
            EntPayResult entPayResult = wxPayService.getEntPayService().entPay(entPayRequest);
            if ("SUCCESS".equals(entPayResult.getResultCode())) { // 如果企业付款成功
                wechatEntPayModel.setStatus("1");
                wechatEntPayModel.setSendDate(new Date());
                wechatEntPayModel.setSendMsg(entPayResult.getReturnMsg());
                payRemote.saveWechatEntPayModel(wechatEntPayModel);
                json.setResultCode(Constant.JSON_SUCCESS_CODE);
                json.setResultMsg("success");
            } else if ("FAIL".equals(entPayResult.getResultCode()) && "SYSTEMERROR".equals(entPayResult.getErrCode())) { // 如果满足当前条件，使用原订单号重试
                entPayResult = wxPayService.getEntPayService().entPay(entPayRequest);
                if ("SUCCESS".equals(entPayResult.getResultCode())) { // 如果企业付款成功
                    wechatEntPayModel.setStatus("1");
                    wechatEntPayModel.setSendDate(new Date());
                    wechatEntPayModel.setSendMsg(entPayResult.getReturnMsg());
                    payRemote.saveWechatEntPayModel(wechatEntPayModel);
                    json.setResultCode(Constant.JSON_SUCCESS_CODE);
                    json.setResultMsg("success");
                }
            } else {
                wechatEntPayModel.setStatus("0");
                wechatEntPayModel.setSendDate(new Date());
                wechatEntPayModel.setSendMsg(entPayResult.getReturnMsg());
                payRemote.saveWechatEntPayModel(wechatEntPayModel);
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("fail");
            }
        } catch (WxPayException e) {
            wechatEntPayModel.setFalseReason(e.getErrCodeDes());
            wechatEntPayModel.setStatus("0");
            payRemote.saveWechatEntPayModel(wechatEntPayModel);
            e.printStackTrace();
            logger.info("提现失败,订单号为:" + wechatEntPayModel.getPartner_trade_no());
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("fail");
        }

        return json;
    }
}
