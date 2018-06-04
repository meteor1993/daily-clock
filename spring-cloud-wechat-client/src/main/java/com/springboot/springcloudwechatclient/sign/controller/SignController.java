package com.springboot.springcloudwechatclient.sign.controller;

import com.alibaba.fastjson.JSON;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.sign.model.UserClockLogModel;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.sign.remote.SignRemote;
import com.springboot.springcloudwechatclient.sign.remote.WechatMpUserRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @program: spring-cloud-wechat-client
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 21:35
 **/
@Controller
@RequestMapping(value = "/wxMp/sign")
public class SignController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    WechatMpUserRemote wechatMpUserRemote;

    @Autowired
    SignRemote signRemote;

    @RequestMapping(value = "/index")
    public String index() {
//        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);
        WxMpUser wxMpUser = new WxMpUser();
        wxMpUser.setOpenId("111111");
        String no = "0";
        logger.info(">>>>>>>>>>>>wechatMpUserRemote.getWechatMpUserByOpenid, openid:" + wxMpUser.getOpenId());
        CommonJson json = wechatMpUserRemote.getWechatMpUserByOpenid(wxMpUser.getOpenId());
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
            // 盘口0 当日打卡第一人
            WechatMpUserModel wechatMpUserModelFirst = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("wechatMpUserModel")), WechatMpUserModel.class);
            // 盘口0 当日打卡第一人打卡记录
            UserClockLogModel userClockLogModel = JSON.parseObject(JSON.toJSONString(dataAmountJson.getResultData().get("userClockLogModel")), UserClockLogModel.class);
            // 盘口0 当日打卡最近10人
            List<WechatMpUserModel> wechatMpUserModelList = JSON.parseArray(JSON.toJSONString(dataAmountJson.getResultData().get("wechatMpUserModelList")), WechatMpUserModel.class);

            if (wechatMpUserModel.getWechatOpenId() != null) { // 已绑定
                CommonJson commonJson = wechatMpUserRemote.signInfo0();
                logger.info(">>>>>>>>>>>>wechatMpUserRemote.signInfo0:" + JSON.toJSONString(commonJson));
                Map<String, Object> map = JSON.parseObject(JSON.toJSONString(commonJson.getResultData()), Map.class);
                map.put("userAccountModel", userAccountModel);
                map.put("userBalance0Sum", userBalance0Sum.substring(0, userBalance0Sum.indexOf(".")));
                map.put("userCount0", userCount0);
                map.put("wechatMpUserModelFirst", wechatMpUserModelFirst);
                map.put("wechatMpUserModelList", wechatMpUserModelList);
                map.put("userClockLogModel", userClockLogModel);
                ContextHolderUtils.getRequest().setAttribute("map", map);
                return "wxmp/sign/index";
            } else { // 未绑定
                return  "wxmp/sign/binding";
            }
        }
        return  "wxmp/sign/binding";
    }
}
