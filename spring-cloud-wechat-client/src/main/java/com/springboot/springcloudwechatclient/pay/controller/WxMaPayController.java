package com.springboot.springcloudwechatclient.pay.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.ProductModel;
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
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import com.springboot.springcloudwechatclient.system.utils.StringUtil;
import org.joda.time.DateTime;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/miniapp/pay")
public class WxMaPayController {

    private final Logger logger = LoggerFactory.getLogger(WxMaPayController.class);

    private static final String payCallUrl = "miniPayNotify/notify";

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    PayRemote payRemote;

    @Autowired
    SignRemote signRemote;

    @Autowired
    NeedClockRemote needClockRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    WxPayService wxPayService;

    @PostMapping(value = "/saveWxPayOrderModel")
    public CommonJson saveWxPayOrderModel(@RequestParam String productNo) {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        this.logger.info(">>>>>>>>>>>>>>WxMaPayController.goPayPage>>>>>>>>>>productNo:" + productNo + ">>>>>>>>>>token:" + token);
        CommonJson json = accountRemote.getProductByProductNo(productNo);
        this.logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);

        CommonJson accountJson = accountRemote.accountInfo(openid);
        this.logger.info(">>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(accountJson));

        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

        if (userAccountModel == null) { // 当前openid未开通账户,则开通账户
            userAccountModel = new UserAccountModel();
            // 设置默认余额
            userAccountModel.setBalance("0.00");
            userAccountModel.setUseBalance0("0.00");
            userAccountModel.setOpenid(openid);
            // 设置默认数据
            userAccountModel.setBalanceSum0("0.00");
            userAccountModel.setTodayBalance0("0.00");
            // 设置打卡标记位
            userAccountModel.setType0("0");
            userAccountModel.setCreateDate(new Date());
            accountRemote.saveAccountModel(userAccountModel);
        }

        ProductModel productModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), ProductModel.class);

        WxPayOrderModel wxPayOrderModel = new WxPayOrderModel();
        wxPayOrderModel.setOpenId(openid);
        wxPayOrderModel.setOrderMoney(Double.parseDouble(productModel.getProductName()));
        wxPayOrderModel.setOrderNo(getOrderNo());
        wxPayOrderModel.setOrderStatus("0");
        wxPayOrderModel.setOrderComment("同意挑战规则并支付挑战金");
        wxPayOrderModel.setProductNo(productNo);
        json = payRemote.saveWxPayOrderModel(wxPayOrderModel);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel:" + JSON.toJSONString(json));
        WxPayOrderModel model = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wxPayOrderModel1")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel>>>>>>>>>>>>>>>>>WxPayOrderModel:" + JSON.toJSONString(model));

        Map<String, Object> map = Maps.newHashMap();
        // 账户余额
        map.put("balance", userAccountModel.getBalance());
        // 商品价格
        map.put("orderMoney", model.getOrderMoney());
        // 订单编号
        map.put("orderNo", model.getOrderNo());
        json.setResultData(map);
        return json;
    }

    /**
     * 使用余额付款
     * @param productNo
     * @return
     */
    @PostMapping(value = "/payByBalance")
    public CommonJson payByBalance(@RequestParam String productNo, @RequestParam String orderNo) throws ParseException {

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String token = ContextHolderUtils.getRequest().getHeader("token");
        this.logger.info(">>>>>>>>>>>>>>WxMaPayController.payByBalance>>>>>>>>>>productNo:" + productNo + ">>>>>>>>>>token:" + token + ">>>>>>>>>>>>>orderNo:" + orderNo);

        // 根据产品号获取产品列表
        CommonJson json = accountRemote.getProductByProductNo(productNo);
        this.logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        ProductModel productModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), ProductModel.class);

        // 获取账户信息
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        CommonJson accountJson = accountRemote.accountInfo(openid);
        this.logger.info(">>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(accountJson));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

        // 先判断当前余额是否足够支付订单，如果不够，直接报错回去
        if (new BigDecimal(userAccountModel.getBalance()).compareTo(new BigDecimal(productModel.getProductName())) == -1) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("余额不足，请先充值~~");
            json.setResultData(null);
            return json;
        } else {
            // 如果余额充足，则继续支付动作
            CommonJson clockConfigJson = signRemote.getClockConfig("0");
            this.logger.info(">>>>>>>>>>>>>>>>>>>>signRemote.getClockConfig:" + JSON.toJSONString(clockConfigJson));
            ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(clockConfigJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);

            // 判断总额是否突破配置上限
            if (new BigDecimal(clockConfigModel.getBalanceTopLine()).compareTo(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(productModel.getProductName()))) == -1) {
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("您的总投入将超出限制，请选择合适的追加额度");
                json.setResultData(null);
                return json;
            }

            userAccountModel.setOrderDate0(new Date());
            // 修改打卡标记位
            userAccountModel.setType0("1");
            // 计算支付成功后余额
            userAccountModel.setBalance(decimalFormat.format(new BigDecimal(userAccountModel.getBalance()).subtract(new BigDecimal(productModel.getProductName())).doubleValue()));
            // 计算支付成功后盘口押金
            userAccountModel.setUseBalance0(decimalFormat.format(new BigDecimal(userAccountModel.getUseBalance0()).add(new BigDecimal(productModel.getProductName())).doubleValue()));

            accountRemote.saveAccountModel(userAccountModel);

            // 获取订单信息
            CommonJson payJson = payRemote.getWxPayOrderModelByOrderNo(orderNo);
            this.logger.info(">>>>>>>>>>>>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(payJson));
            WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(payJson.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);
            wxPayOrderModel.setOrderStatus("1");
            wxPayOrderModel.setPayTime(new Date());
            wxPayOrderModel.setUpdateDate(new Date());
            wxPayOrderModel.setPayType("balance");
            payRemote.saveWxPayOrderModel(wxPayOrderModel);

            // 保存账户变动log
            UserAccountLogModel userAccountLogModel = new UserAccountLogModel();
            userAccountLogModel.setTypeFlag("1");
            userAccountLogModel.setType("4");
            userAccountLogModel.setOpenid(openid);
            userAccountLogModel.setCreateDate(new Date());
            userAccountLogModel.setAmount(productModel.getProductName());
            userAccountLogModel.setNo("0");
            accountRemote.saveAccountModelLog(userAccountLogModel);

            // 获取待打卡数据
            CommonJson needClockJson = needClockRemote.getByOpenidAndNeedDate(openid, new Date());
            this.logger.info(">>>>>>>>>>>>>>>>>>>>needClockRemote.getByOpenidAndNeedDate:" + JSON.toJSONString(needClockJson));
            NeedClockUserModel needClockUserModel = JSON.parseObject(JSON.toJSONString(needClockJson.getResultData().get("needClockUserModel")), NeedClockUserModel.class);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
                } else {
                    // 设置预计打卡时间为明日
                    needClockUserModel1.setNeedDate(new DateTime().plusDays(1).toLocalDateTime().toDate());
                }

                needClockRemote.saveNeedClock(needClockUserModel1);
                // 如果缓存存在
                if (redisTemplate.hasKey(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0)) {
                    redisTemplate.opsForHash().put(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0, openid, JSON.toJSONString(needClockUserModel1));
                } else {
                    // 如果缓存不存在
                    Map<String, Object> map = Maps.newHashMap();
                    map.put(openid, JSON.toJSONString(needClockUserModel1));
                    redisTemplate.opsForHash().putAll(sdf.format(new Date()) + "," + Constant.TODAY_NEED_SIGN_USER_0, map);
                }

            } else {
                // 如果当前时间小于系统预设时间，更新用户打卡金额
                if (nowTime < startTime) {
                    // 更新数据库
                    needClockUserModel.setUseBalance(userAccountModel.getUseBalance0());
                    needClockRemote.saveNeedClock(needClockUserModel);
                    // 更新缓存 不更新缓存信息，会将当日打卡缓存覆盖
//                    redisTemplate.opsForHash().put(Constant.TODAY_NEED_SIGN_USER_0, openid, needClockUserModel);

                } else {
                    // 如果大于系统预设时间，创建第二日打卡数据
                    NeedClockUserModel needClockUserModel1 = new NeedClockUserModel();
                    needClockUserModel1.setCreateDate(new Date());
                    needClockUserModel1.setOpenid(openid);
                    needClockUserModel1.setNo("0");
                    needClockUserModel1.setUseBalance(userAccountModel.getUseBalance0());
                    needClockUserModel1.setNeedDate(new DateTime().plusDays(1).toLocalDateTime().toDate());
                    needClockRemote.saveNeedClock(needClockUserModel1);
                    // 更新缓存 不更新缓存信息，会将当日打卡缓存覆盖
//                    redisTemplate.opsForHash().put(Constant.TODAY_NEED_SIGN_USER_0, openid, needClockUserModel);
                }

            }


            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("支付成功");
            json.setResultData(null);
        }

        return json;
    }

    @PostMapping(value = "/unifiedOrder")
    public CommonJson unifiedOrder(@RequestParam String productNo, @RequestParam String orderNo) throws WxPayException {

        String token = ContextHolderUtils.getRequest().getHeader("token");
        // 获取openid
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>>>>>>>>>>>>WxMaPayController.payByBalance>>>>>>>>>>productNo:" + productNo + ">>>>>>>>>>token:" + token + ">>>>>>>>>>>>>orderNo:" + orderNo);

        // 根据产品号获取产品列表
        CommonJson json = accountRemote.getProductByProductNo(productNo);
        this.logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        ProductModel productModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), ProductModel.class);

        // 获取订单信息
        CommonJson payJson = payRemote.getWxPayOrderModelByOrderNo(orderNo);
        this.logger.info(">>>>>>>>>>>>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(payJson));
        WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(payJson.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);

        CommonJson accountJson = accountRemote.accountInfo(openid);
        this.logger.info(">>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(accountJson));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

        // 获取全局配置
        CommonJson clockConfigJson = signRemote.getClockConfig("0");
        this.logger.info(">>>>>>>>>>>>>>>>>>>>signRemote.getClockConfig:" + JSON.toJSONString(clockConfigJson));
        ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(clockConfigJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);

        // 判断总额是否突破配置上限
        if (new BigDecimal(clockConfigModel.getBalanceTopLine()).compareTo(new BigDecimal(userAccountModel.getBalance()).add(new BigDecimal(wxPayOrderModel.getOrderMoney()))) == -1) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("您的总投入将超出限制，请选择合适的追加额度");
            json.setResultData(null);
            return json;
        }

        int batch = (wxPayOrderModel.getPayTimes() == null ? 0 : wxPayOrderModel.getPayTimes()) + 1;
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String basePath = request.getScheme()+"://"+request.getServerName()+request.getContextPath()+"/";

        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = WxPayUnifiedOrderRequest.newBuilder().build();
        // 随机数
        wxPayUnifiedOrderRequest.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
        // openid
        wxPayUnifiedOrderRequest.setOpenid(openid);
        // 商品描述
        wxPayUnifiedOrderRequest.setBody(wxPayOrderModel.getOrderComment());
        // 商户订单号
        wxPayUnifiedOrderRequest.setOutTradeNo(wxPayOrderModel.getOrderNo());
        // 标价金额
        wxPayUnifiedOrderRequest.setTotalFee(new BigDecimal(wxPayOrderModel.getOrderMoney()).multiply(new BigDecimal("100")).intValue());
        // 终端IP
//        wxPayUnifiedOrderRequest.setSpbillCreateIp(getIp(request));
        wxPayUnifiedOrderRequest.setSpbillCreateIp("172.16.13.50");
        // 通知地址
        wxPayUnifiedOrderRequest.setNotifyUrl(basePath + payCallUrl);
                //.setNotifyURL(basePath + payCallUrl);
        // 交易类型
        wxPayUnifiedOrderRequest.setTradeType("JSAPI");

        WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(wxPayUnifiedOrderRequest);

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>result:" + JSON.toJSONString(result));

        wxPayOrderModel.setTxOrderNo(result.getPrepayId());
        wxPayOrderModel.setPayTimes(batch);
        wxPayOrderModel.setPayTime(new Date());

        json = payRemote.saveWxPayOrderModel(wxPayOrderModel);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel:" + JSON.toJSONString(json));
        wxPayOrderModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wxPayOrderModel1")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel>>>>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));

        String signType = "MD5";
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replaceAll("-", "");
        Map<String, String> configMap = Maps.newHashMap();
        configMap.put("appId", this.wxPayService.getConfig().getAppId());
        configMap.put("timeStamp", timeStamp);
        configMap.put("nonceStr", nonceStr);
        configMap.put("package", "prepay_id=" + result.getPrepayId());
        configMap.put("signType", signType);
        String paySign = SignUtils.createSign(configMap, signType, this.wxPayService.getConfig().getMchKey(), false);
//        String paySign = SignUtils.createSign(configMap, signType, this.wxMaService.getWxMaConfig().getSecret(), false);
        this.logger.info(">>>>>>>>>>map:" + JSON.toJSONString(configMap) + ">>>>>>>paySign:" + paySign);
        Map<String, Object> map = Maps.newHashMap();
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("package", result.getPrepayId());
        map.put("signType", signType);
        map.put("paySign", paySign);

        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("下单成功");
        json.setResultData(map);
        return json;
    }

    /**
     * 微信支付成功后数据处理
     * @param wxPayOrderId
     * @return
     */
    @PostMapping(value = "/minipaySuccess")
    public CommonJson minipaySuccess(@RequestParam String wxPayOrderId) {
//        String token = ContextHolderUtils.getRequest().getHeader("token");
//        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
//        this.logger.info(">>>>>>>>>>>>>>WxMaPayController.payByBalance>>>>>>>>>>token:" + token + ">>>>>>>>>>>>>openid:" + openid);
//
//
//        CommonJson wxPayOrderJson = payRemote.getWxPayOrderModelById(wxPayOrderId);
//
//        WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(wxPayOrderJson.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);

        return null;
    }

    /**
     * 生成订单号
     * @return
     */
    private String getOrderNo(){
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
