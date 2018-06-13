package com.springboot.springcloudwechatclient.miniapp.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.sign.model.UserClockLogModel;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.sign.remote.WechatMpUserRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @program: spring-cloud-wechat-client
 * @description:
 * @author: weishiyao
 * @create: 2018-06-06 21:35
 **/
@RestController
@RequestMapping(value = "/miniapp/clock")
public class ClockController {

    private final Logger logger = LoggerFactory.getLogger(ClockController.class);

    @Autowired
    WechatMpUserRemote wechatMpUserRemote;

    @Autowired
    SignRemote signRemote;

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping(value = "/index")
    public CommonJson index() {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        this.logger.info(">>>ClockController.index>>>>>>>token:" + token);
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:ss:mm");
        String no = "0";
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.getWechatMpUserByOpenid, openid:" + openid);
        CommonJson json = wechatMpUserRemote.getWechatMpUserByOpenid(openid);
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.getWechatMpUserByOpenid:" + JSON.toJSONString(json));
        CommonJson dataAmountJson = signRemote.dataAmount(no);
        logger.info(">>>>>>>>>>>>signRemote.dataAmount:" + JSON.toJSONString(dataAmountJson));
        if (Constant.JSON_SUCCESS_CODE.equals(json.getResultCode())) {
            // 获取微信绑定用户信息
            WechatMpUserModel wechatMpUserModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
            // 获取相关账户信息
            UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("userAccountModel")), UserAccountModel.class);
            // 盘口0数据汇总
            // 盘口0总押金
            String userBalance0Sum = dataAmountJson.getResultData().get("userBalance0Sum").toString();
            // 盘口0 当日总账户数（参与打卡人数）
            String userCount0 = dataAmountJson.getResultData().get("userCount0").toString();
            // 盘口0 当日总需打卡数
            String needClockUserSum = dataAmountJson.getResultData().get("needClockUserSum").toString();
            // 盘口0 当日事实打卡人数
            String todayClockUserSum = dataAmountJson.getResultData().get("todayClockUserSum").toString();
            // 盘口0 当日打卡第一人
            WechatMpUserModel wechatMpUserModelFirst = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
            // 盘口0 当日打卡第一人打卡记录
            UserClockLogModel userClockLogModel = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("userClockLogModel")), UserClockLogModel.class);
            if (userClockLogModel != null) {
                userClockLogModel.setCreateStringDate(simpleDateFormat.format(userClockLogModel.getCreateDate()));
            }
            // 盘口0 当日打卡最近10人
            List<WechatMpUserModel> wechatMpUserModelList = JSON.parseArray(JSON.toJSONString(dataAmountJson.getResultData().get("wechatMpUserModelList")), WechatMpUserModel.class);

            if (wechatMpUserModel != null) { // 已绑定
                CommonJson commonJson = wechatMpUserRemote.signInfo0();
                logger.info(">>>>>>>>>>>>wechatMpUserRemote.signInfo0:" + JSON.toJSONString(commonJson));
                Map<String, Object> map = JSON.parseObject(JSON.toJSONString(commonJson.getResultData()), Map.class);
                map.put("userAccountModel", userAccountModel);
                map.put("userBalance0Sum", userBalance0Sum.substring(0, userBalance0Sum.indexOf(".")));
                map.put("userCount0", userCount0);
                map.put("wechatMpUserModelFirst", wechatMpUserModelFirst);
                map.put("wechatMpUserModelList", wechatMpUserModelList);
                map.put("userClockLogModel", userClockLogModel);
                map.put("needClockUserSum", needClockUserSum);
                map.put("todayClockUserSum", todayClockUserSum);
                json.setResultCode(Constant.JSON_SUCCESS_CODE);
                json.setResultData(map);
                return json;
            } else { // 未绑定
                Map<String, Object> map = Maps.newHashMap();
                map.put("userAccountModel", userAccountModel);
                map.put("userBalance0Sum", userBalance0Sum.substring(0, userBalance0Sum.indexOf(".")));
                map.put("userCount0", userCount0);
                map.put("wechatMpUserModelFirst", wechatMpUserModelFirst);
                map.put("wechatMpUserModelList", wechatMpUserModelList);
                map.put("userClockLogModel", userClockLogModel);
                map.put("needClockUserSum", needClockUserSum);
                map.put("todayClockUserSum", todayClockUserSum);
                json.setResultCode(Constant.JSON_SUCCESS_CODE);
                json.setResultData(map);
                return json;
            }
        }
        return json;
    }

    /**
     * 查询所有的产品
     * @return
     */
    @PostMapping(value = "/findProductList")
    public CommonJson findProductList() {
        return accountRemote.findProductList();
    }


}
