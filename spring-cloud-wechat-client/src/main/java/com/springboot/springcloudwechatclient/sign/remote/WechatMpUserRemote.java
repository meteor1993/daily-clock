package com.springboot.springcloudwechatclient.sign.remote;

import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: spring-cloud-wechat-client
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 21:43
 **/
@FeignClient(name= "daily-clock-server")
public interface WechatMpUserRemote {

    /**
     * 根据openid获取微信用户绑定信息
     * @param openid
     * @return
     */
    @PostMapping(value = "/api/getWechatMpUserByOpenid")
    CommonJson getWechatMpUserByOpenid(@RequestParam(value = "openid") String openid);
}
