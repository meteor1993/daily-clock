package com.springboot.dailyclock.pay.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.dailyclock.pay.dao.WxPayOrderDao;
import com.springboot.dailyclock.pay.model.WxPayOrderModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by weisy on 2018/5/22
 */
@RestController
@RequestMapping(path = "/payApi")
public class PayController {

    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    @Autowired
    WxPayOrderDao wxPayOrderDao;

    /**
     * 更新、保存微信订单
     * @param wxPayOrderModel
     * @return
     */
    @PostMapping(value = "/saveWxPayOrderModel")
    public CommonJson saveWxPayOrderModel(@RequestBody WxPayOrderModel wxPayOrderModel) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>saveWxPayOrderModel, wxPayOrderModel:" + JSON.toJSONString(wxPayOrderModel));
        CommonJson json = new CommonJson();
        WxPayOrderModel wxPayOrderModel1 = wxPayOrderDao.save(wxPayOrderModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put("wxPayOrderModel1", wxPayOrderModel1);
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("成功");
        return json;
    }

    /**
     * 根据orderId查询相关订单
     * @param wxPayOrderId
     * @return
     */
    @PostMapping(value = "/getWxPayOrderModelById")
    public CommonJson getWxPayOrderModelById(@RequestParam String wxPayOrderId) {
        CommonJson json = new CommonJson();
        WxPayOrderModel wxPayOrderModel = wxPayOrderDao.getByIdIs(wxPayOrderId);
        Map<String, Object> map = Maps.newHashMap();
        map.put("wxPayOrderModel", wxPayOrderModel);
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("成功");
        return json;
    }

    /**
     * 根据orderId查询相关订单
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getWxPayOrderModelByOrderNo")
    public CommonJson getWxPayOrderModelByOrderNo(@RequestParam String orderNo) {
        CommonJson json = new CommonJson();
        WxPayOrderModel wxPayOrderModel = wxPayOrderDao.getByOrderNo(orderNo);
        Map<String, Object> map = Maps.newHashMap();
        map.put("wxPayOrderModel", wxPayOrderModel);
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("成功");
        return json;
    }

}