package com.springboot.springcloudwechatclient.admin.remote;

import com.springboot.springcloudwechatclient.admin.model.AdminInfoModel;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 管理员操作 远程调用类
 */
@FeignClient(name= "daily-clock-server")
public interface AdminRemote {

    /**
     * 根据创建日期得到AdminInfo
     * @param date
     * @return
     */
    @PostMapping(value = "/admin/getAdminInfoByCreateDate")
    CommonJson getAdminInfoByCreateDate(@RequestParam(value = "date") String date);

    /**
     * 保存/更新 adminInfoModel
     * @param adminInfoModel
     * @return
     */
    @PostMapping(value = "/admin/saveAdminInfo")
    CommonJson saveAdminInfo(@RequestBody AdminInfoModel adminInfoModel);

    /**
     * 管理员数据查询
     * @return
     */
    @PostMapping(value = "/admin/getDataInfo")
    CommonJson getDataInfo();

    @PostMapping(value = "/admin/getUserByMobile")
    CommonJson getUserByMobile(@RequestParam(value = "mobile") String mobile);
}
