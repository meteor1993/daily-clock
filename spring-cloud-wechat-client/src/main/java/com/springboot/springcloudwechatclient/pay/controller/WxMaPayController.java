package com.springboot.springcloudwechatclient.pay.controller;

import com.alibaba.fastjson.JSON;
import com.springboot.springcloudwechatclient.account.model.ProductModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.pay.model.WxPayOrderModel;
import com.springboot.springcloudwechatclient.pay.remote.PayRemote;
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

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value = "/miniapp/pay")
public class WxMaPayController {

    private final Logger logger = LoggerFactory.getLogger(WxMaPayController.class);

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    PayRemote payRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping(value = "/goPayPage")
    public CommonJson goPayPage(@RequestParam String productNo) {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        this.logger.info(">>>>>>>>>>>>>>WxMaPayController.goPayPage>>>>>>>>>>productNo:" + productNo + ">>>>>>>>>>token:" + token);
        CommonJson json = accountRemote.getProductByProductNo(productNo);
        this.logger.info(">>>>>>>>>>>>accountRemote.getProductByProductNo:" + JSON.toJSONString(json));
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
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

        json.setResultData(null);
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
