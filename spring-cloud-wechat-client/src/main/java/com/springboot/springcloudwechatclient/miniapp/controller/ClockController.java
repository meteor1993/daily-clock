package com.springboot.springcloudwechatclient.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage.Data;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.sign.model.ClockConfigModel;
import com.springboot.springcloudwechatclient.sign.model.UserClockLogModel;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.sign.remote.WechatMpUserRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    WxMaMsgService wxMaMsgService;


    @Autowired
    WechatMpUserRemote wechatMpUserRemote;

    @Autowired
    SignRemote signRemote;

    @Autowired
    AccountRemote accountRemote;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping(value = "/index")
    public CommonJson index() throws ParseException {
        String token = ContextHolderUtils.getRequest().getHeader("token");
        this.logger.info(">>>ClockController.index>>>>>>>token:" + token);
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String no = "0";
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.getWechatMpUserByOpenid, openid:" + openid);
        // 获取绑定微信信息
        CommonJson json = wechatMpUserRemote.getWechatMpUserByOpenid(openid);
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.getWechatMpUserByOpenid:" + JSON.toJSONString(json));
        // 获取汇总数据
        CommonJson dataAmountJson = signRemote.dataAmount(no);
        logger.info(">>>>>>>>>>>>signRemote.dataAmount:" + JSON.toJSONString(dataAmountJson));
        // 获取盘口配置信息
        CommonJson configJson = signRemote.getClockConfig(no);
        this.logger.info(">>>>>>>>>>>>signRemote.getClockConfig:" + JSON.toJSONString(configJson));
        ClockConfigModel clockConfigModel = JSON.parseObject(JSON.toJSONString(configJson.getResultData().get("clockConfigModel")), ClockConfigModel.class);
        this.logger.info(">>>>>>>>>>>>signRemote.getClockConfig>>>>>>>>>>>clockConfigModel:" + JSON.toJSONString(clockConfigModel));
        long toDate;
        boolean clockFlag;
        // 比较当前时间是否超过盘口配置的时间
        // 如果当前时间小于盘口配置开始时间
        Date startDate = sdf1.parse(sdf.format(new Date()) + " " + clockConfigModel.getClockStartTime());
        Date endDate = sdf1.parse(sdf.format(new Date()) + " " + clockConfigModel.getClockEndTime());
        if (new Date().getTime() < startDate.getTime()) {
            // 开始时间为盘口配置的当日时间
            toDate = startDate.getTime();
            clockFlag = false;
        } else if (new Date().getTime() > startDate.getTime() && new Date().getTime() < endDate.getTime()) { // 如果当前时间处于盘口配置的开始和结束之间之间
            // 开始时间为盘口配置的当日时间
            toDate = startDate.getTime();
            clockFlag = true;
        } else {
            // 开始时间为第二日的盘口配置开始时间
            toDate = sdf1.parse(sdf.format(new DateTime().plusDays(1).toDate()) + " " + clockConfigModel.getClockStartTime()).getTime();
            clockFlag = false;
        }

        if (Constant.JSON_SUCCESS_CODE.equals(json.getResultCode())) {
            // 获取微信绑定用户信息
            WechatMpUserModel wechatMpUserModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
            // 获取相关账户信息
            UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(json.getResultData().get("userAccountModel")), UserAccountModel.class);
            // 盘口0数据汇总


//            String userBalance0Sum = dataAmountJson.getResultData().get("userBalance0Sum").toString();
            // 盘口0总押金
            String userBalance0Sum = (dataAmountJson.getResultData().get("userBalance0Sum") == null ? "0.00" : dataAmountJson.getResultData().get("userBalance0Sum").toString());
            // 盘口0 当日总账户数（参与打卡人数）
//            String userCount0 = dataAmountJson.getResultData().get("userCount0").toString();
            String userCount0 = (dataAmountJson.getResultData().get("userCount0") == null ? "" : dataAmountJson.getResultData().get("userCount0").toString());
            // 盘口0 当日总需打卡数
//            String needClockUserSum = dataAmountJson.getResultData().get("needClockUserSum").toString();
            String needClockUserSum = (dataAmountJson.getResultData().get("needClockUserSum") == null ? "" : dataAmountJson.getResultData().get("needClockUserSum").toString());
            // 盘口0 当日实时打卡人数
//            String todayClockUserSum = dataAmountJson.getResultData().get("todayClockUserSum").toString();
            String todayClockUserSum = (dataAmountJson.getResultData().get("todayClockUserSum") == null ? "" : dataAmountJson.getResultData().get("todayClockUserSum").toString());
            // 盘口0 当日实时未打卡人数
//            String todayUnClockUserSum = dataAmountJson.getResultData().get("todayUnClockUserSum").toString();
            String todayUnClockUserSum = (dataAmountJson.getResultData().get("todayUnClockUserSum") == null ? "" : dataAmountJson.getResultData().get("todayUnClockUserSum").toString());
            // 盘口0 当日打卡第一人
            WechatMpUserModel wechatMpUserModelFirst = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
            // 盘口0 当日打卡第一人打卡记录
            UserClockLogModel userClockLogModel = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("userClockLogModel")), UserClockLogModel.class);
            if (userClockLogModel != null) {
                userClockLogModel.setCreateStringDate(simpleDateFormat.format(userClockLogModel.getCreateDate()));
            }
            // 毅力之星微信信息
            WechatMpUserModel maxContinuousClockUserWechat = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("maxContinuousClockUserWechat")), WechatMpUserModel.class);
            // 毅力之星账户信息
            UserAccountModel maxContinuousClockUserAccount = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("maxContinuousClockUserAccount")), UserAccountModel.class);
            // 盘口0 当日打卡最近10人
            List<WechatMpUserModel> wechatMpUserModelList = JSON.parseArray(JSON.toJSONString(dataAmountJson.getResultData().get("wechatMpUserModelList")), WechatMpUserModel.class);
            this.logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>wechatMpUserModelList:" + JSON.toJSONString(wechatMpUserModelList));

            Map<String, Object> map = Maps.newHashMap();
            map.put("userAccountModel", userAccountModel);
            map.put("userBalance0Sum", userBalance0Sum.substring(0, userBalance0Sum.indexOf(".")));
            map.put("userCount0", userCount0);
            map.put("wechatMpUserModelFirst", wechatMpUserModelFirst);
            map.put("wechatMpUserModelList", wechatMpUserModelList);
            map.put("userClockLogModel", userClockLogModel);
            map.put("needClockUserSum", needClockUserSum);
            map.put("todayClockUserSum", todayClockUserSum);
            map.put("todayUnClockUserSum", todayUnClockUserSum);
            map.put("maxContinuousClockUserWechat", maxContinuousClockUserWechat);
            map.put("maxContinuousClockUserAccount", maxContinuousClockUserAccount);
            map.put("nowDate", new Date().getTime());
            map.put("toDate", toDate);
            map.put("clockFlag", clockFlag);
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultData(map);
            return json;
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

    @PostMapping(value = "/clock")
    public CommonJson clock(@RequestParam String formid) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String token = ContextHolderUtils.getRequest().getHeader("token");
        String openid = (String) redisTemplate.opsForHash().get(token, Constant.WX_MINIAPP_OPENID);
        this.logger.info(">>>ClockController.index>>>>>>>token:" + token + ">>>>>>>>>>openid:" + openid + ">>>>>>>>>>formid:" + formid);
        CommonJson clockJson = signRemote.clock(openid, "0", Constant.CLOCK_TYPE_1);
        if ("1".equals(clockJson.getResultCode())) {
            WxMaTemplateMessage wxMaTemplateMessage = WxMaTemplateMessage.builder().build();
            wxMaTemplateMessage.setToUser(openid);
            wxMaTemplateMessage.setTemplateId("vL3RVoKBr3q_ZI3jhE84zvl_VZ4q-9XJJzEUdB0DpFA");
            wxMaTemplateMessage.setFormId(formid);

            List<Data> list = new ArrayList<>();

            Data data1 = new Data("keyword1", simpleDateFormat.format(new Date()));
            list.add(data1);
            Data data2 = new Data("keyword2", "打卡成功");
            list.add(data2);

            wxMaTemplateMessage.setData(list);

            try {
                wxMaMsgService.sendTemplateMsg(wxMaTemplateMessage);
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }

        return clockJson;
    }
}
