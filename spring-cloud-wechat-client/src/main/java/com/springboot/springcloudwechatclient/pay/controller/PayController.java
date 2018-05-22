package com.springboot.springcloudwechatclient.pay.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.springboot.springcloudwechatclient.account.model.ProductModel;
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
public class PayController {

    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    private static final String payCallUrl = "wxMp/pay/payNotifyUrl";

    @Autowired
    WxPayService wxPayService;

    @Autowired
    PayRemote payRemote;

    @Autowired
    AccountRemote accountRemote;

    /**
     * 保存微信订单
     * @param productNo
     * @return
     */
    @PostMapping("/saveWxPayOrderModel")
    public CommonJson saveWxPayOrderModel(@RequestParam String productNo) {
        CommonJson json = accountRemote.getProductByProductNo(productNo);
        logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        ProductModel productModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), ProductModel.class);
        logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo>>>>>>>>>>>>productModel:" + JSON.toJSONString(productModel));

        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);

        WxPayOrderModel wxPayOrderModel = new WxPayOrderModel();
        wxPayOrderModel.setOpenId(wxMpUser.getOpenId());
        wxPayOrderModel.setOrderMoney(Double.parseDouble(productModel.getProductName()));
        wxPayOrderModel.setOrderTime(new Date());
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
    public CommonJson unifiedOrder(HttpServletRequest request) throws WxPayException {
        String wxPayOrderId = (String) request.getSession().getAttribute("wxPayOrderId");
        CommonJson json = payRemote.getWxPayOrderModelById(wxPayOrderId);
        logger.info(">>>>>>>>>>>>payRemote.getWxPayOrderModelById:" + JSON.toJSONString(json));
        WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("productModel")), WxPayOrderModel.class);
        logger.info(">>>>>>>>>>>>payRemote.getWxPayOrderModelById>>>>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));

        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);

        int batch = (wxPayOrderModel.getPayTimes()==null ? 0:wxPayOrderModel.getPayTimes())+1;

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
        wxPayUnifiedOrderRequest.setNotifyURL(basePath + payCallUrl);
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
