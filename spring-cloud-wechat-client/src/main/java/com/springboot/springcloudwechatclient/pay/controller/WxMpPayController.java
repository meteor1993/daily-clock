package com.springboot.springcloudwechatclient.pay.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.springboot.springcloudwechatclient.account.model.ProductModel;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.pay.model.WxPayOrderModel;
import com.springboot.springcloudwechatclient.pay.remote.PayRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import com.springboot.springcloudwechatclient.system.utils.StringUtil;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by weisy on 2018/5/22
 */
@Controller
@RequestMapping(value = "/wxMp/pay")
public class WxMpPayController {

    private static final Logger logger = LoggerFactory.getLogger(WxMpPayController.class);

    private static final String payCallUrl = "wxMp/pay/payNotifyUrl";

    @Autowired
    WxPayService wxPayService;

    @Autowired
    PayRemote payRemote;

    @Autowired
    AccountRemote accountRemote;

    @RequestMapping(value = "/goPayPage")
    public String goPayPage() {
//        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        WxPayOrderModel wxPayOrderModel = (WxPayOrderModel) ContextHolderUtils.getSession().getAttribute("wxPayOrderModel");
        ContextHolderUtils.getRequest().setAttribute("orderNo", wxPayOrderModel.getOrderNo());
        ContextHolderUtils.getRequest().setAttribute("orderMoney", wxPayOrderModel.getOrderMoney());
//        CommonJson json = accountRemote.accountInfo(wxMpUser.getOpenId());
        CommonJson json = accountRemote.accountInfo("111111");
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.accountInfo:" + JSON.toJSONString(json));
        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("userAccountModel")), UserAccountModel.class);
        ContextHolderUtils.getRequest().setAttribute("balance", userAccountModel.getBalance());
        return "wxmp/pay/payPage";
    }

    /**
     * 保存微信订单
     * @param productNo
     * @return
     */
    @PostMapping("/saveWxPayOrderModel")
    @ResponseBody
    public CommonJson saveWxPayOrderModel(@RequestParam String productNo) {
        CommonJson json = accountRemote.getProductByProductNo(productNo);
        logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        ProductModel productModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), ProductModel.class);
        logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo>>>>>>>>>>>>productModel:" + JSON.toJSONString(productModel));

//        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);

        WxPayOrderModel wxPayOrderModel = new WxPayOrderModel();
//        wxPayOrderModel.setOpenId(wxMpUser.getOpenId());
        wxPayOrderModel.setOpenId("111111");
        wxPayOrderModel.setOrderMoney(Double.parseDouble(productModel.getProductName()));
        wxPayOrderModel.setOrderNo(getOrderNo());
        wxPayOrderModel.setOrderStatus("0");
        wxPayOrderModel.setOrderComment("同意挑战规则并支付挑战金");
        wxPayOrderModel.setProductNo(productNo);
        json = payRemote.saveWxPayOrderModel(wxPayOrderModel);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel:" + JSON.toJSONString(json));
        WxPayOrderModel model = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wxPayOrderModel1")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel>>>>>>>>>>>>>>>>>WxPayOrderModel:" + JSON.toJSONString(model));

        ContextHolderUtils.getSession().setAttribute("wxPayOrderModel", model);
        json.setResultData(null);
        return json;
    }

    /**
     * 微信统一下单
     * @param request
     * @return
     * @throws WxPayException
     */
    @PostMapping("/unifiedOrder")
    @ResponseBody
    public CommonJson unifiedOrder(HttpServletRequest request) throws WxPayException {
        String wxPayOrderId = (String) request.getSession().getAttribute("wxPayOrderId");
        CommonJson json = payRemote.getWxPayOrderModelById(wxPayOrderId);
        logger.info(">>>>>>>>>>>>payRemote.getWxPayOrderModelById:" + JSON.toJSONString(json));
        WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.getWxPayOrderModelById>>>>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));

        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);

        int batch = (wxPayOrderModel.getPayTimes()==null ? 0:wxPayOrderModel.getPayTimes()) + 1;

        String basePath = request.getScheme()+"://"+request.getServerName()+request.getContextPath()+"/";;
        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = WxPayUnifiedOrderRequest.newBuilder().build();
        // 随机数
        wxPayUnifiedOrderRequest.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
        // openid
        wxPayUnifiedOrderRequest.setOpenid(wxMpUser.getOpenId());
        // 商品描述
        wxPayUnifiedOrderRequest.setBody(wxPayOrderModel.getOrderComment());
        // 商户订单号
        wxPayUnifiedOrderRequest.setOutTradeNo(getOrderNo() + batch);
        // 标价金额
        wxPayUnifiedOrderRequest.setTotalFee(new BigDecimal(wxPayOrderModel.getOrderMoney()).multiply(new BigDecimal("100")).intValue());
        // 终端IP
        wxPayUnifiedOrderRequest.setSpbillCreateIp(getIp(request));
        // 通知地址
        wxPayUnifiedOrderRequest.setNotifyUrl(basePath + payCallUrl);
                //setNotifyURL(basePath + payCallUrl);
        // 交易类型
        wxPayUnifiedOrderRequest.setTradeType("JSAPI");

        WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(wxPayUnifiedOrderRequest);

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>result:" + JSON.toJSONString(result));

        wxPayOrderModel.setPayTime(new Date());
        wxPayOrderModel.setTxOrderNo(result.getPrepayId());
        wxPayOrderModel.setPayTime(new Date());
        wxPayOrderModel.setPayTimes(batch);

        json = payRemote.saveWxPayOrderModel(wxPayOrderModel);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel:" + JSON.toJSONString(json));
        wxPayOrderModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel>>>>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));

        json.setResultCode(Constant.SUCCESS_CODE);
        json.setResultMsg("下单成功");

        return json;
    }

    /**
     * 支付回调
     * @param xmlData
     * @throws WxPayException
     */
    @RequestMapping(value = "/payNotifyUrl")
    @Deprecated
    public void getOrderNotifyResult(@RequestParam String xmlData) throws WxPayException {

        WxPayOrderNotifyResult result = wxPayService.getOrderNotifyResult(xmlData);

        CommonJson json = payRemote.getWxPayOrderModelByOrderNo(result.getOutTradeNo().substring(0, 28));
        logger.info(">>>>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(json));
        WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel>>>>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));

        wxPayOrderModel.setOrderStatus("1");
        wxPayOrderModel.setPaySuccessTime(result.getTimeEnd());
        wxPayOrderModel.setTransactionId(result.getTransactionId());
        wxPayOrderModel.setUpdateDate(new Date());

        json = payRemote.saveWxPayOrderModel(wxPayOrderModel);

        logger.info(">>>>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(json));

        wxPayOrderModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);

        logger.info(">>>>>>>>>>>>payRemote.saveWxPayOrderModel>>>>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));

    }

    /**
     * 企业付款
     * @return
     */
//    @PostMapping(value = "/entPay")
//    @ResponseBody
//    public CommonJson entPay(@RequestParam String balance) throws WxPayException {
//        CommonJson json = new CommonJson();
//        int flag1 = new BigDecimal(balance).compareTo(new BigDecimal("1"));
//
//        if (flag1 != 1) {
//            json.setResultCode(Constant.JSON_ERROR_CODE);
//            json.setResultMsg("提现金额必须大于1元");
//            return json;
//        }
//
//        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
//
//        json = accountRemote.accountInfo(wxMpUser.getOpenId());
//        logger.info(">>>>>>>>>>>>accountRemote.accountInfo:" + JSON.toJSONString(json));
//        UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("userAccountModel")), UserAccountModel.class);
//        logger.info(">>>>>>>>>>>>accountRemote.accountInfo>>>>>>>>>>>>>userAccountModel:" + JSON.toJSONString(userAccountModel));
//
//        int flag = new BigDecimal(balance).compareTo(new BigDecimal(userAccountModel.getBalance()));
//        if (flag == -1) {
//            json.setResultCode(Constant.JSON_ERROR_CODE);
//            json.setResultMsg("提现金额小于账户余额");
//            json.setResultData(null);
//            return json;
//        }
//
//        // 保存企业付款记录
//        WechatEntPayModel wechatEntPayModel = new WechatEntPayModel();
//        wechatEntPayModel.setAmount(new BigDecimal(balance).multiply(new BigDecimal("100")).intValue());
//        wechatEntPayModel.setCreateDate(new Date());
//        wechatEntPayModel.setCheck_name("NO_CHECK");
//        wechatEntPayModel.setDesc("开心打卡账户提现");
//        wechatEntPayModel.setDevice_info("");
//        wechatEntPayModel.setNonce_str(UUID.randomUUID().toString().replaceAll("-", ""));
//        wechatEntPayModel.setOpenid(wxMpUser.getOpenId());
//        wechatEntPayModel.setPartner_trade_no(getOrderNo());
//        wechatEntPayModel.setSpbill_create_ip(getIp(ContextHolderUtils.getRequest()));
//        wechatEntPayModel.setStatus("0");
//
//        json = payRemote.saveWechatEntPayModel(wechatEntPayModel);
//        logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(json));
//        wechatEntPayModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
//        logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));
//
//        // 企业付款
//        WxEntPayRequest wxEntPayRequest = WxEntPayRequest.newBuilder().build();
//        // 设备号，非必填
//        wxEntPayRequest.setDeviceInfo("");
//        // 订单号
//        wxEntPayRequest.setPartnerTradeNo(wechatEntPayModel.getPartner_trade_no());
//        // openid
//        wxEntPayRequest.setOpenid(wechatEntPayModel.getOpenid());
//        // 校验用户姓名选项 NO_CHECK：不校验真实姓名  FORCE_CHECK：强校验真实姓名
//        wxEntPayRequest.setCheckName(wechatEntPayModel.getCheck_name());
//        // 金额
//        wxEntPayRequest.setAmount(wechatEntPayModel.getAmount());
//        // 企业付款描述信息
//        wxEntPayRequest.setDescription(wechatEntPayModel.getDesc());
//        // Ip地址
//        wxEntPayRequest.setSpbillCreateIp(wechatEntPayModel.getSpbill_create_ip());
//
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            WxEntPayResult wxEntPayResult = wxPayService.entPay(wxEntPayRequest);
//            if ("SUCCESS".equals(wxEntPayResult.getResultCode())) { // 如果企业付款成功
//                wechatEntPayModel.setStatus("1");
//                wechatEntPayModel.setSendDate(simpleDateFormat.parse(wxEntPayResult.getPaymentTime()));
//                wechatEntPayModel.setSendMsg(wxEntPayResult.getReturnMsg());
//                json = payRemote.saveWechatEntPayModel(wechatEntPayModel);
//                logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(json));
//                wechatEntPayModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
//                logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));
//            } else if ("FAIL".equals(wxEntPayResult.getResultCode()) && "SYSTEMERROR".equals(wxEntPayResult.getErrCode())){ // 如果满足当前条件，使用原订单号重试
//                wxEntPayResult = wxPayService.entPay(wxEntPayRequest);
//                if ("SUCCESS".equals(wxEntPayResult.getResultCode())) { // 如果企业付款成功
//                    wechatEntPayModel.setStatus("1");
//                    wechatEntPayModel.setSendDate(simpleDateFormat.parse(wxEntPayResult.getPaymentTime()));
//                    wechatEntPayModel.setSendMsg(wxEntPayResult.getReturnMsg());
//                    json = payRemote.saveWechatEntPayModel(wechatEntPayModel);
//                    logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(json));
//                    wechatEntPayModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
//                    logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));
//                }
//            } else {
//                wechatEntPayModel.setStatus("0");
//                wechatEntPayModel.setSendDate(simpleDateFormat.parse(wxEntPayResult.getPaymentTime()));
//                wechatEntPayModel.setSendMsg(wxEntPayResult.getReturnMsg());
//                json = payRemote.saveWechatEntPayModel(wechatEntPayModel);
//                logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(json));
//                wechatEntPayModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
//                logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));
//            }
//        } catch (WxPayException e) {
//            wechatEntPayModel.setFalseReason(e.getErrCodeDes());
//            wechatEntPayModel.setStatus("0");
//            json = payRemote.saveWechatEntPayModel(wechatEntPayModel);
//            logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(json));
//            wechatEntPayModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
//            logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));
//            e.printStackTrace();
//            logger.info("发放失败,订单号为:" + wechatEntPayModel.getPartner_trade_no());
//            json.setResultCode(Constant.JSON_ERROR_CODE);
//            json.setResultMsg("提现失败，请联系客服处理");
//            json.setResultData(null);
//            return json;
//        } catch (ParseException e) {
//            wechatEntPayModel.setStatus("0");
//            json = payRemote.saveWechatEntPayModel(wechatEntPayModel);
//            logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel:" + JSON.toJSONString(json));
//            wechatEntPayModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatEntPayModel1")), WechatEntPayModel.class);
//            logger.info(">>>>>>>>>>>>payRemote.saveWechatEntPayModel>>>>>>>>>>>>>wechatEntPayModel:" + JSON.toJSONString(wechatEntPayModel));
//            e.printStackTrace();
//            logger.info("发放失败,订单号为:" + wechatEntPayModel.getPartner_trade_no());
//            json.setResultCode(Constant.JSON_ERROR_CODE);
//            json.setResultMsg("提现失败，请联系客服处理");
//            json.setResultData(null);
//            return json;
//        }
//        return json;
//    }

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
