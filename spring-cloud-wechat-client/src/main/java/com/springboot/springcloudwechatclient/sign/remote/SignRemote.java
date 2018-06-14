package com.springboot.springcloudwechatclient.sign.remote;

import com.springboot.springcloudwechatclient.system.model.CommonJson;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: spring-cloud-wechat-client
 * @description: 打卡远程调用
 * @author: weishiyao
 * @create: 2018-05-20 18:54
 **/
@FeignClient(name= "daily-clock-server")
public interface SignRemote {

    /**
     * 阿里云发短信
     * @param sessionId
     * @param mobile
     * @return
     */
    @PostMapping(value = "/api/sendSMS")
    CommonJson sendSMS(@RequestParam(value = "sessionId") String sessionId, @RequestParam(value = "mobile") String mobile);

    /**
     * 绑定微信信息
     * @param openid
     * @param sessionId
     * @param mobile
     * @param code
     * @return
     */
    @PostMapping(value = "/api/binding")
    CommonJson binding(@RequestParam(value = "openid") String openid, @RequestParam(value = "sessionId") String sessionId, @RequestParam(value = "mobile") String mobile, @RequestParam(value = "code") String code);

    /**
     * 早晨打卡
     * @param wxMpUser
     * @param no
     * @return
     */
    @PostMapping(value = "/api/clock")
    CommonJson clock(@RequestBody WxMpUser wxMpUser, @RequestParam(value = "no") String no);

    /**
     * 盘口0数据汇总
     * @param no
     * @return
     */
    @PostMapping(value = "/api/dataAmount")
    CommonJson dataAmount(@RequestParam(value = "no") String no);

    /**
     * 获取盘口配置
     * @param no
     * @return
     */
    @PostMapping(value = "/api/getClockConfig")
    CommonJson getClockConfig(@RequestParam(value = "no") String no);
}
