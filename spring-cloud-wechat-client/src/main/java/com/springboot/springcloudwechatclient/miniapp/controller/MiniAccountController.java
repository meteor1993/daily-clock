package com.springboot.springcloudwechatclient.miniapp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountLogModel;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.pay.model.WechatEntPayModel;
import com.springboot.springcloudwechatclient.pay.remote.PayRemote;
import com.springboot.springcloudwechatclient.sign.model.ClockConfigModel;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import com.springboot.springcloudwechatclient.system.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/miniapp/miniAccount")
public class MiniAccountController {

    private final Logger logger = LoggerFactory.getLogger(MiniAccountController.class);

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    SignRemote signRemote;

    @Autowired
    PayRemote payRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    WxPayService wxPayService;

    @PostMapping(value = "/accountInfo")
    public CommonJson accountInfo() {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>MiniAccountController.accountInfo>>>>>>>>>token:" + token + ">>>>>openid:" + openid);
        CommonJson configJson = signRemote.getClockConfig("0");
        this.logger.info(">>>>>>>>>>>>>accountRemote.getClockConfig:" + JSON.toJSONString(configJson));
        ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(configJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);

        CommonJson accountJson = accountRemote.accountInfo(openid);
        this.logger.info(">>>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(accountJson));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

        Map<String, Object> map = Maps.newHashMap();
        map.put("clockConfigModel", clockConfigModel);
        map.put("userAccountModel", userAccountModel);

        CommonJson json = new CommonJson();
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;

    }

    @PostMapping(value = "/getMoney")
    public CommonJson getMoney(@RequestParam String money) {
        CommonJson json = new CommonJson();

        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>MiniAccountController.getMoney>>>>>>>>>token:" + token + ">>>>>opnid:" + openid + ">>>>>>>>>>>money:" + money);

        // 获取全局配置信息
        CommonJson configJson = signRemote.getClockConfig("0");
        this.logger.info(">>>>>>>>>>>>>accountRemote.getClockConfig:" + JSON.toJSONString(configJson));
        ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(configJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);

        // 获取账户信息
        CommonJson accountJson = accountRemote.accountInfo(openid);
        this.logger.info(">>>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(accountJson));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

        // 提现金额大于账户余额
        if (new BigDecimal(userAccountModel.getBalance()).compareTo(new BigDecimal(money)) == -1) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("提现金额大于账户余额");
            json.setResultData(null);
            return json;
        }

        // 提现金额大于提现上限
        if (new BigDecimal(money).compareTo(new BigDecimal(clockConfigModel.getGetMoneyTopLine())) == -1) {

            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("您的提现申请超过上限，已提交管理员审批，请耐心等待");
            json.setResultData(null);
            return json;
        }



        userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).subtract(new BigDecimal(money)).toString());
        userAccountModel.setUpdateDate(new Date());
        // 记录出账日志
        UserAccountLogModel userAccountLogModel = new UserAccountLogModel();
        userAccountLogModel.setAmount(money);
        userAccountLogModel.setCreateDate(new Date());
        userAccountLogModel.setNo("0");
        userAccountLogModel.setType("3");
        userAccountLogModel.setOpenid(openid);

        WechatEntPayModel  wechatEntPayModel = new WechatEntPayModel();
        wechatEntPayModel.setCreateDate(new Date());
        wechatEntPayModel.setDevice_info("");
        wechatEntPayModel.setNonce_str(UUID.randomUUID().toString().replaceAll("-", ""));
        wechatEntPayModel.setPartner_trade_no(getIdGen());
        wechatEntPayModel.setOpenid(openid);
        wechatEntPayModel.setCheck_name("NO_CHECK");
        wechatEntPayModel.setAmount(new BigDecimal(money).multiply(new BigDecimal("100")).intValue());
        wechatEntPayModel.setDesc("开心打卡");
//        wechatEntPayModel.setSpbill_create_ip("192.168.0.1");
        wechatEntPayModel.setSpbill_create_ip(getIp(ContextHolderUtils.getRequest()));
        wechatEntPayModel.setStatus("0");
        CommonJson wechatEntyPayJson = payRemote.saveWechatEntPayModel(wechatEntPayModel);
        this.logger.info(">>>>>>>>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(wechatEntyPayJson));
        wechatEntPayModel = JSON.parseObject(JSON.toJSONString(wechatEntyPayJson.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
        this.logger.info(">>>>>>>>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));


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
                accountRemote.saveAccountModel(userAccountModel);
                accountRemote.saveAccountModelLog(userAccountLogModel);
                json.setResultCode(Constant.JSON_SUCCESS_CODE);
                json.setResultMsg("success");
            } else if ("FAIL".equals(entPayResult.getResultCode()) && "SYSTEMERROR".equals(entPayResult.getErrCode())) { // 如果满足当前条件，使用原订单号重试
                entPayResult = wxPayService.getEntPayService().entPay(entPayRequest);
                if ("SUCCESS".equals(entPayResult.getResultCode())) { // 如果企业付款成功
                    wechatEntPayModel.setStatus("1");
                    wechatEntPayModel.setSendDate(new Date());
                    wechatEntPayModel.setSendMsg(entPayResult.getReturnMsg());
                    accountRemote.saveAccountModel(userAccountModel);
                    accountRemote.saveAccountModelLog(userAccountLogModel);
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

    /**
     * 首页列表分页查询
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/findMpAccountList")
    public CommonJson findMpAccountList(@RequestParam int page, @RequestParam int size) {
        CommonJson accountJson = accountRemote.findMpAccountList(page, size);
//        this.logger.info(">>>>>>>>>>>>>>>>>accountRemote.findMpAccountList:" + JSON.toJSONString(accountJson.getResultData()));
//        JSONArray list = JSON.parseArray(JSON.toJSONString(accountJson.getResultData().get("list")));
//        for (Object maps : list) {
//            Map<String, Object> map = (Map<String, Object>) maps;
//            String clockDate0 = (String) map.get("0");
//        }

//        this.logger.info(">>>>>>>>>>>>>>>>>accountRemote.findMpAccountList:" + list.get(0));
        return accountJson;
    }


    private String getIdGen(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = simpleDateFormat.format(new Date());
        return str + String.valueOf(System.nanoTime());
    }

    /**
     * 获取真实ip
     * @param request
     * @return
     */
    private static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtil.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtil.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
