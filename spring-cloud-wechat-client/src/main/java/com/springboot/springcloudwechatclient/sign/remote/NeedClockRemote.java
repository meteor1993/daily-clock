package com.springboot.springcloudwechatclient.sign.remote;

import com.springboot.springcloudwechatclient.sign.model.NeedClockUserModel;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name= "daily-clock-server")
public interface NeedClockRemote {

    /**
     * 根据参数查询需打卡人
     * @param openid
     * @param needDate
     * @return
     */
    @PostMapping(value = "/api/needClock/getByOpenidAndNeedDate")
    CommonJson getByOpenidAndNeedDate(@RequestParam(value = "openid") String openid, @RequestParam(value = "needDate") String needDate);

    /**
     * 删除需打卡人
     * @param needClockUserModel
     * @return
     */
    @PostMapping(value = "/api/needClock/deleteNeedClock")
    CommonJson deleteNeedClock(@RequestBody NeedClockUserModel needClockUserModel);

    /**
     * 保存需打卡人
     * @param needClockUserModel
     * @return
     */
    @PostMapping(value = "/api/needClock/saveNeedClock")
    CommonJson saveNeedClock(@RequestBody NeedClockUserModel needClockUserModel);
}
