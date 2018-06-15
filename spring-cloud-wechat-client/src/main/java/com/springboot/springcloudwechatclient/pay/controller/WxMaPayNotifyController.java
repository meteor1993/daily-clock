package com.springboot.springcloudwechatclient.pay.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.springboot.springcloudwechatclient.pay.model.WxPayOrderModel;
import com.springboot.springcloudwechatclient.pay.remote.PayRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(value = "/miniPayNotify")
public class WxMaPayNotifyController {

    private final Logger logger = LoggerFactory.getLogger(WxMaPayNotifyController.class);

    @Autowired
    WxPayService wxPayService;

    @Autowired
    PayRemote payRemote;

    /**
     * 微信支付通知请求，被微信调用
     * @param request
     * @return
     */
    @PostMapping(value = "/notify")
    public CommonJson notify(HttpServletRequest request) {
        this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>");
        CommonJson json = new CommonJson();
        try {
            // 更新微信订单
            String restxml = HttpUtils.getBodyString(request.getReader()) ;
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>restxml:" + restxml);
            WxPayOrderNotifyResult wxPayOrderNotifyResult = wxPayService.parseOrderNotifyResult(restxml);
            CommonJson wxPayJson = payRemote.getWxPayOrderModelByOrderNo(wxPayOrderNotifyResult.getOutTradeNo().substring(0, 27));
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>payRemote.getWxPayOrderModelByOrderNo:" + JSON.toJSONString(wxPayJson));
            WxPayOrderModel wxPayOrderModel = JSON.parseObject(JSON.toJSONString(wxPayJson.getResultData().get("wxPayOrderModel")), WxPayOrderModel.class);
            this.logger.info(">>>>>>>>>>>>>>WxMaPayNotifyController.notify>>>>>>>>>wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));
            wxPayOrderModel.setOrderStatus("1");
            wxPayOrderModel.setPaySuccessTime(wxPayOrderNotifyResult.getTimeEnd());
            wxPayOrderModel.setTransactionId(wxPayOrderNotifyResult.getTransactionId());
            payRemote.saveWxPayOrderModel(wxPayOrderModel);
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("success");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WxPayException e) {
            e.printStackTrace();
        }
        return json;
    }
}
