package com.springboot.springcloudwechatclient.pay.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.ProductModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(value = "/miniapp/pay")
public class WxMaPayController {

    private final Logger logger = LoggerFactory.getLogger(WxMaPayController.class);

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
    public CommonJson payByBalance(@RequestParam String productNo, @RequestParam String orderNo) {

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String token = ContextHolderUtils.getRequest().getHeader("token");
        this.logger.info(">>>>>>>>>>>>>>WxMaPayController.payByBalance>>>>>>>>>>productNo:" + productNo + ">>>>>>>>>>token:" + token + ">>>>>>>>>>>>>orderNo:" + orderNo);

        CommonJson json = accountRemote.getProductByProductNo(productNo);
        this.logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        ProductModel productModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), ProductModel.class);

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

            CommonJson payJson = payRemote.getWxPayOrderModelByOrderNo(orderNo);
            this.logger.info(">>>>>>>>>>>>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(payJson));
            WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(payJson.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);
            wxPayOrderModel.setOrderStatus("1");
            wxPayOrderModel.setPayTime(new Date());
            wxPayOrderModel.setUpdateDate(new Date());
            wxPayOrderModel.setPayType("balance");
            payRemote.saveWxPayOrderModel(wxPayOrderModel);

            CommonJson needClockJson = needClockRemote.getByOpenidAndNeedDate(openid, new Date());
            this.logger.info(">>>>>>>>>>>>>>>>>>>>needClockRemote.getByOpenidAndNeedDate:" + JSON.toJSONString(needClockJson));
            NeedClockUserModel needClockUserModel = JSON.parseObject(JSON.toJSONString(needClockJson.getResultData().get("needClockUserModel")), NeedClockUserModel.class);
            // 如果待打卡列表为空
            if (needClockUserModel == null) {

            } else {

            }


            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("支付成功");
            json.setResultData(null);
        }

        return json;
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
