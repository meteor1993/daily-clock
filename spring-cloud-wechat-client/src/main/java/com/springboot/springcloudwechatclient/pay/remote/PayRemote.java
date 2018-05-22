package com.springboot.springcloudwechatclient.pay.remote;

import com.springboot.springcloudwechatclient.pay.model.WechatEntPayModel;
import com.springboot.springcloudwechatclient.pay.model.WxPayOrderModel;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by weisy on 2018/5/22
 */
@FeignClient(name= "daily-clock-server")
public interface PayRemote {

    /**
     * 保存微信订单
     * @param wxPayOrderModel
     * @return
     */
    @PostMapping(value = "/payApi/saveWxPayOrderModel")
    CommonJson saveWxPayOrderModel(@RequestBody WxPayOrderModel wxPayOrderModel);

    @PostMapping(value = "/payApi/getWxPayOrderModelById")
    CommonJson getWxPayOrderModelById(@RequestParam(value = "wxPayOrderId") String wxPayOrderId);

    @PostMapping(value = "/payApi/getWxPayOrderModelByOrderNo")
    CommonJson getWxPayOrderModelByOrderNo(@RequestParam(value = "orderNo") String orderNo);

    @PostMapping(value = "/payApi/saveWechatEntPayModel")
    CommonJson saveWechatEntPayModel(@RequestBody WechatEntPayModel wechatEntPayModel);
}
