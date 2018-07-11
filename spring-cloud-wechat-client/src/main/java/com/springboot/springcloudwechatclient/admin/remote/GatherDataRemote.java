package com.springboot.springcloudwechatclient.admin.remote;

import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 数据统计 远程调用类
 */
@FeignClient(name= "gather-data-server")
public interface GatherDataRemote {
    /**
     * 统计
     * @return
     */
    @PostMapping(value = "/admin/gatherData")
    CommonJson gatherData();
}
