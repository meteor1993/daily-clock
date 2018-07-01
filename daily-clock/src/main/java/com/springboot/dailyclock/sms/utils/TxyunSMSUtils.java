package com.springboot.dailyclock.sms.utils;

import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TxyunSMSUtils {

    // 单例模式
    private TxyunSMSUtils() {}
    private static final TxyunSMSUtils txyunSMSUtils = new TxyunSMSUtils();
    //静态工厂方法
    public static TxyunSMSUtils getInstance() {
        return txyunSMSUtils;
    }

    private final Logger logger = LoggerFactory.getLogger(TxyunSMSUtils.class);

    // 短信应用SDK AppID
    private final int appid = 1400107975; // 1400开头

    // 短信应用SDK AppKey
    private final String appkey = "4cb0af69ec7dbe30689283604fa888b6";

    // 短信模板ID，需要在短信应用中申请
    private final int templateId = 148278; // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请

    // 签名
    private final String smsSign = ""; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

    public boolean sendTxyunSMS(String mobile, String code) {
        String[] phoneNumbers = {mobile};
        String[] params = {code};
        try {
            SmsMultiSender msender = new SmsMultiSender(appid, appkey);
            SmsMultiSenderResult result =  msender.sendWithParam("86", phoneNumbers, templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            this.logger.info(">>>>>>>>>>>>>>>>>TxyunSMSUtils.sendTxyunSMS:" + result);
            return true;
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
