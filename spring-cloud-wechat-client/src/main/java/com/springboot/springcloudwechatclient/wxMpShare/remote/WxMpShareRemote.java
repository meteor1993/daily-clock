package com.springboot.springcloudwechatclient.wxMpShare.remote;

import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.wxMpShare.model.WxMpShareModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name= "daily-clock-server")
public interface WxMpShareRemote {

    /**
     * 根据unionID获取实体
     * @param unionId
     * @return
     */
    @PostMapping(value = "/wxMpShare/getAllByUnionId")
    CommonJson getAllByUnionId(@RequestParam(value = "unionId") String unionId);

    /**
     * 根据unionID获取实体
     * @param preUnionId
     * @return
     */
    @PostMapping(value = "/wxMpShare/findAllByPreUnionId")
    CommonJson findAllByPreUnionId(@RequestParam(value = "preUnionId") String preUnionId);

    /**
     * 保存或更新WxMpShareModel
     * @param wxMpShareModel
     * @return
     */
    @PostMapping(value = "/wxMpShare/saveWxMpShareModel")
    CommonJson saveWxMpShareModel(@RequestBody WxMpShareModel wxMpShareModel);
}
