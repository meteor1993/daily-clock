package com.springboot.springcloudwechatclient.account.remote;

import com.springboot.springcloudwechatclient.account.model.UserAccountLogModel;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by weisy on 2018/5/22
 */
@FeignClient(name= "daily-clock-server")
public interface AccountRemote {
    /**
     * 查询个人账户信息
     * @param openid
     * @return
     */
    @PostMapping(value = "/api/accountInfo")
    CommonJson accountInfo(@RequestParam(value = "openid") String openid);

    /**
     * 查询所有的产品
     * @return
     */
    @PostMapping(value = "/api/findProductList")
    CommonJson findProductList();

    /**
     * 根据ProductNo查询Product
     * @return
     */
    @PostMapping(value = "/api/getProductByProductNo")
    CommonJson getProductByProductNo(@RequestParam(value = "productNo") String productNo);

    @PostMapping(value = "/api/saveAccountModel")
    CommonJson saveAccountModel(@RequestBody UserAccountModel userAccountModel);

    /**
     * 保存用户账户日志
     * @param userAccountLogModel
     * @return
     */
    @PostMapping(value = "/api/saveAccountModelLog")
    CommonJson saveAccountModelLog(@RequestBody UserAccountLogModel userAccountLogModel);

    @PostMapping(value = "/api/getAccountModelLogByOrderNo")
    CommonJson getAccountModelLogByOrderNo(@RequestParam(value = "orderNo") String orderNo);

    /**
     * 账户中心数据查询
     * @param openid
     * @param no
     * @return
     */
    @PostMapping(value = "/api/accountCenter")
    CommonJson accountCenter(@RequestParam(value = "openid") String openid, @RequestParam(value = "no") String no);

    /**
     * 分页查询首页列表
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/api/findMpAccountList")
    CommonJson findMpAccountList(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size);

    /**
     * 分页查询当前用户进出账记录
     * @param openid
     * @param date
     * @return
     */
    @PostMapping(value = "/api/findUserAccountLogList")
    CommonJson findUserAccountLogList(@RequestParam(value = "openid") String openid, @RequestParam(value = "date") String date);

}
