package com.springboot.springcloudwechatclient.pay.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountLogModel;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.pay.model.WxPayOrderModel;
import com.springboot.springcloudwechatclient.pay.remote.PayRemote;
import com.springboot.springcloudwechatclient.sign.model.ClockConfigModel;
import com.springboot.springcloudwechatclient.sign.model.NeedClockUserModel;
import com.springboot.springcloudwechatclient.sign.remote.NeedClockRemote;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.HttpUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/miniPayNotify")
public class WxMaPayNotifyController {

    private final Logger logger = LoggerFactory.getLogger(WxMaPayNotifyController.class);

    @Autowired
    WxPayService wxPayService;

    @Autowired
    PayRemote payRemote;

    @Autowired
    NeedClockRemote needClockRemote;

    @Autowired
    SignRemote signRemote;

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 微信支付通知请求，被微信调用
     * @param request
     * @return
     */
    @PostMapping(value = "/notify")
    public CommonJson notify(HttpServletRequest request) throws ParseException {
        this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>");
        CommonJson json = new CommonJson();
        try {
            // 更新微信订单
            String restxml = HttpUtils.getBodyString(request.getReader()) ;
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>restxml:" + restxml);
            WxPayOrderNotifyResult wxPayOrderNotifyResult = wxPayService.parseOrderNotifyResult(restxml);
            CommonJson wxPayJson = payRemote.getWxPayOrderModelByOrderNo(wxPayOrderNotifyResult.getOutTradeNo());
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(wxPayJson));
            WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(wxPayJson.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));
            wxPayOrderModel.setOrderStatus("1");
            wxPayOrderModel.setPaySuccessTime(wxPayOrderNotifyResult.getTimeEnd());
            wxPayOrderModel.setTransactionId(wxPayOrderNotifyResult.getTransactionId());
            wxPayOrderModel.setUpdateDate(new Date());
            wxPayOrderModel.setPayType("miniapp");
            payRemote.saveWxPayOrderModel(wxPayOrderModel);

            String openid = wxPayOrderModel.getOpenId();

            // 获取账户数据
            CommonJson accountJson = accountRemote.accountInfo(openid);
            this.logger.info(">>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(accountJson));
            UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

            CommonJson logJson = accountRemote.getAccountModelLogByOrderNo(wxPayOrderModel.getOrderNo());
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>accountRemote.getAccountModelLogByOrderNo:" + JSON.toJSONString(logJson));
            UserAccountLogModel userAccountLogModel = JSON.parseObject(JSON.toJSONString(logJson.getResultData().get("userAccountLogModel")), UserAccountLogModel.class);

            CommonJson clockConfigJson = signRemote.getClockConfig("0");
            this.logger.info(">>>>>>>>>>>>>>>>>>>>signRemote.getClockConfig:" + JSON.toJSONString(clockConfigJson));
            ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(clockConfigJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);

            // 保存账户进账数据
            if (userAccountLogModel == null) {
                // 更新账户数据 只更新一次
                userAccountModel.setUseBalance0(new BigDecimal(userAccountModel.getUseBalance0() == null ? "0" : userAccountModel.getUseBalance0()).add(new BigDecimal(String.valueOf(wxPayOrderModel.getOrderMoney()))).toString());
                userAccountModel.setOrderDate0(wxPayOrderModel.getPayTime());
                userAccountModel.setUpdateDate(new Date());
                userAccountModel.setType0("1");

                // 操作上级账户
                if (userAccountModel.getPreOpenid() != null && "1".equals(userAccountModel.getPreOpenidFlag())) { // 操作小程序上级账户
                    CommonJson preAccountJson = accountRemote.accountInfo(userAccountModel.getPreOpenid());
                    this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>preAccountJson:" + JSON.toJSONString(preAccountJson));
                    UserAccountModel preUserAccountModel = JSON.parseObject(JSON.toJSONString(preAccountJson.getResultData().get("userAccountModel")), UserAccountModel.class);
                    // 更新上级账户余额
                    preUserAccountModel.setBalance(new BigDecimal(clockConfigModel.getBonus()).add(new BigDecimal(preUserAccountModel.getBalance() == null ? "0" : preUserAccountModel.getBalance())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                    this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>preUserAccountModel.openid:" + preUserAccountModel.getOpenid());

                    // 更新上级账户奖励金
//                    preUserAccountModel.setRewardBalance(new BigDecimal(clockConfigModel.getRewardBalanceLines()).add(new BigDecimal(preUserAccountModel.getRewardBalance() == null ? "0" : preUserAccountModel.getRewardBalance())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                    UserAccountLogModel userAccountLogModel1 = new UserAccountLogModel();
                    userAccountLogModel1.setOpenid(preUserAccountModel.getOpenid());
                    userAccountLogModel1.setCreateDate(new Date());
                    userAccountLogModel1.setNo("0");
                    userAccountLogModel1.setAmount(clockConfigModel.getBonus());
                    userAccountLogModel1.setType("7");

                    accountRemote.saveAccountModelLog(userAccountLogModel1);
                    accountRemote.saveAccountModel(preUserAccountModel);
                } else if(true) { // 操作公众号上级账户

                }

                userAccountModel.setPreOpenidFlag("0");
                accountRemote.saveAccountModel(userAccountModel);

                userAccountLogModel = new UserAccountLogModel();
                userAccountLogModel.setType("1");
                userAccountLogModel.setNo("0");
                userAccountLogModel.setAmount(String.valueOf(wxPayOrderModel.getOrderMoney()));
                userAccountLogModel.setCreateDate(new Date());
                userAccountLogModel.setOpenid(wxPayOrderModel.getOpenId());
                userAccountLogModel.setOrderNo(wxPayOrderModel.getOrderNo());
                userAccountLogModel.setTypeFlag("1");
                accountRemote.saveAccountModelLog(userAccountLogModel);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                // 获取待打卡数据
                this.logger.info(">>>>>>>>>>>>>>>>>>>>needClockRemote.getByOpenidAndNeedDate>>>>>>>>>>openid:" + openid + ", newDate:" + new Date().toString());
                CommonJson needClockJson = needClockRemote.getByOpenidAndNeedDate(openid, sdf.format(new Date()));
                this.logger.info(">>>>>>>>>>>>>>>>>>>>needClockRemote.getByOpenidAndNeedDate:" + JSON.toJSONString(needClockJson));
                NeedClockUserModel needClockUserModel = JSON.parseObject(JSON.toJSONString(needClockJson.getResultData().get("needClockUserModel")), NeedClockUserModel.class);

                Long startTime = simpleDateFormat.parse(sdf.format(new Date()) + " " + clockConfigModel.getClockStartTime()).getTime();

                Long nowTime = new Date().getTime();

                // 如果待打卡列表为空，用户为首次充值
                if (needClockUserModel == null) {
                    NeedClockUserModel needClockUserModel1 = new NeedClockUserModel();
                    needClockUserModel1.setCreateDate(new Date());
                    needClockUserModel1.setOpenid(openid);
                    needClockUserModel1.setNo("0");
                    needClockUserModel1.setUseBalance(userAccountModel.getUseBalance0());

                    // 如果当前时间小于系统预设时间，则可以当日打卡
                    if (nowTime < startTime) {
                        needClockUserModel1.setNeedDate(new Date());
                        // 如果缓存存在
                        if (redisTemplate.hasKey(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0)) {
                            logger.info("缓存不存在当前key为：" + sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0 + "openid为：" + openid + "写入redis内容为:" + JSON.toJSONString(needClockUserModel1));
                            redisTemplate.opsForHash().put(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0, openid, JSON.toJSONString(needClockUserModel1));
                        } else {
                            // 如果缓存不存在
                            logger.info("缓存存在当前key为：" + sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0 + "openid为：" + openid + "写入redis内容为:" + JSON.toJSONString(needClockUserModel1));
                            Map<String, Object> map = Maps.newHashMap();
                            map.put(openid, JSON.toJSONString(needClockUserModel1));
                            redisTemplate.opsForHash().putAll(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0, map);
                        }
                    } else {
                        // 设置预计打卡时间为明日
                        needClockUserModel1.setNeedDate(new DateTime().plusDays(1).toLocalDateTime().toDate());
                        // 如果缓存存在
                        if (redisTemplate.hasKey(sdf.format(new DateTime().plusDays(1).toLocalDateTime().toDate()) + "," + Constant.TODAY_NEED_SIGN_USER_0)) {
                            logger.info("缓存不存在当前key为：" + sdf.format(new DateTime().plusDays(1).toLocalDateTime().toDate()) + "," + Constant.TODAY_NEED_SIGN_USER_0 + "openid为：" + openid + "写入redis内容为:" + JSON.toJSONString(needClockUserModel1));
                            redisTemplate.opsForHash().put(sdf.format(new DateTime().plusDays(1).toLocalDateTime().toDate()) + "," + Constant.TODAY_NEED_SIGN_USER_0, openid, JSON.toJSONString(needClockUserModel1));
                        } else {
                            // 如果缓存不存在
                            logger.info("缓存存在当前key为：" + sdf.format(new DateTime().plusDays(1).toLocalDateTime().toDate()) + "," + Constant.TODAY_NEED_SIGN_USER_0 + "openid为：" + openid + "写入redis内容为:" + JSON.toJSONString(needClockUserModel1));
                            Map<String, Object> map = Maps.newHashMap();
                            map.put(openid, JSON.toJSONString(needClockUserModel1));
                            redisTemplate.opsForHash().putAll(sdf.format(new DateTime().plusDays(1).toLocalDateTime().toDate()) + "," + Constant.TODAY_NEED_SIGN_USER_0, map);
                        }
                    }

                    needClockRemote.saveNeedClock(needClockUserModel1);

                    // 设定24小时过期
                    redisTemplate.expire(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0, 24, TimeUnit.HOURS);
                } else {
                    // 如果当前时间小于系统预设时间，更新用户打卡金额
                    if (nowTime < startTime) {
                        // 更新数据库
                        needClockUserModel.setUseBalance(userAccountModel.getUseBalance0());
                        needClockRemote.saveNeedClock(needClockUserModel);
                    } else {
                        // 如果大于系统预设时间，创建第二日打卡数据
                        needClockUserModel.setOpenid(openid);
                        needClockUserModel.setNo("0");
                        needClockUserModel.setUseBalance(userAccountModel.getUseBalance0());
                        needClockUserModel.setNeedDate(new DateTime().plusDays(1).toLocalDateTime().toDate());
                        needClockRemote.saveNeedClock(needClockUserModel);
                    }
                }
            }
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("success");
        } catch (WxPayException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
