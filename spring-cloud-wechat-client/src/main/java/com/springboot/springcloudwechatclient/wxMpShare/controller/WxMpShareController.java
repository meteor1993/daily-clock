package com.springboot.springcloudwechatclient.wxMpShare.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.account.model.UserAccountLogModel;
import com.springboot.springcloudwechatclient.account.model.UserAccountModel;
import com.springboot.springcloudwechatclient.account.remote.AccountRemote;
import com.springboot.springcloudwechatclient.sign.model.WechatMpUserModel;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.ContextHolderUtils;
import com.springboot.springcloudwechatclient.system.utils.StringUtil;
import com.springboot.springcloudwechatclient.wxMpShare.model.WxMpShareModel;
import com.springboot.springcloudwechatclient.wxMpShare.remote.WxMpShareRemote;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wxMp/wxMpShare")
public class WxMpShareController {

    private final Logger logger = LoggerFactory.getLogger(WxMpShareController.class);

    @Autowired
    WxMpShareRemote wxMpShareRemote;

    @Autowired
    AccountRemote accountRemote;

    @RequestMapping("/goIndex")
    public String goIndex(@RequestParam(name = "preUnionId", required=false) String preUnionId) {
        WxMpUser wxMpUser = (WxMpUser) ContextHolderUtils.getSession().getAttribute(Constant.WX_MP_USER);

        this.logger.info("WxMpShareController.goIndex>>>>>>>>>>>>wxMpUser:" + JSON.toJSONString(wxMpUser));

        CommonJson wxMpShareJson = wxMpShareRemote.getAllByUnionId(wxMpUser.getUnionId());

        this.logger.info("WxMpShareController.goIndex>>>>>>>>>>>>>>wxMpShareJson:" + JSON.toJSONString(wxMpShareJson));

        WxMpShareModel wxMpShareModel = JSON.parseObject(JSON.toJSONString(wxMpShareJson.getResultData().get("wxMpShareModel")), WxMpShareModel.class);

        // 判断当前用户是否参加过活动
        if (wxMpShareModel == null) { // 如果未参加过活动
            /**
             * 1.新增分享表信息
             * 2.判断当前用户账户是否存在，如果存在，完成账户信息操作
             */
            wxMpShareModel = new WxMpShareModel();
            wxMpShareModel.setCreateDate(new Date());
            wxMpShareModel.setOpenid(wxMpUser.getOpenId());
            wxMpShareModel.setRedBagAmount("5");
            wxMpShareModel.setRedBagFlag("1");
            wxMpShareModel.setUnionId(wxMpUser.getUnionId());
            wxMpShareModel.setWxMpHeadImg(wxMpUser.getHeadImgUrl());
            wxMpShareModel.setWxMpName(wxMpUser.getNickname());
            if (StringUtil.isNotEmpty(preUnionId)) { // 如果上级账户存在
                wxMpShareModel.setPreUnionId(preUnionId);
                wxMpShareModel.setPreAmount("5");
                wxMpShareModel.setPreAmountFlag("1");
            }

            CommonJson accountJson = accountRemote.getUserAccountByUnionId(wxMpUser.getUnionId());
            this.logger.info("WxMpShareController.goIndex>>>>>>>>>>>>accountJson:" + JSON.toJSONString(accountJson));
            UserAccountModel userAccountModel = JSON.parseObject(JSON.toJSONString(accountJson.getResultData().get("userAccountModel")), UserAccountModel.class);

            if (userAccountModel != null) { // 如果当前用户已存在账户信息
                // 红包直接增加账户打卡押金
                userAccountModel.setUseBalance0(new BigDecimal(userAccountModel.getUseBalance0()).add(new BigDecimal("5")).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                // 更改标记位
                wxMpShareModel.setRedBagFlag("0");
                // 新增入账log
                UserAccountLogModel userAccountLogModel = new UserAccountLogModel();
                userAccountLogModel.setType("8");
                userAccountLogModel.setAmount("5");
                userAccountLogModel.setNo("0");
                userAccountLogModel.setOpenid(userAccountModel.getOpenid());
                userAccountLogModel.setCreateDate(new Date());
                // 保存信息
                accountRemote.saveAccountModelLog(userAccountLogModel);
                accountRemote.saveAccountModel(userAccountModel);
            }
            // 保存分享信息
            CommonJson shareJson = wxMpShareRemote.saveWxMpShareModel(wxMpShareModel);
            this.logger.info("WxMpShareController.goIndex>>>>>>>>>>>>shareJson:" + JSON.toJSONString(shareJson));
            wxMpShareModel= JSON.parseObject(JSON.toJSONString(shareJson.getResultData().get("wxMpShareModel")), WxMpShareModel.class);

        } else { // 如果已参与过活动
            /**
             * 1.分享表信息不做修改
             * 2.用户账户不做改动
             */
        }

        // 查询所有下级用户
        CommonJson nextJson = wxMpShareRemote.findAllByPreUnionId(wxMpShareModel.getUnionId());
        this.logger.info("WxMpShareController.goIndex>>>>>>>>>>>>nextJson:" + JSON.toJSONString(nextJson));
        List<WxMpShareModel> wxMpShareModelList = JSONArray.parseArray(JSON.toJSONString(nextJson.getResultData().get("wxMpShareModelList")), WxMpShareModel.class);
        // 将自己添加在list中第一个
        wxMpShareModelList.add(0, wxMpShareModel);

        Map<String, Object> map = Maps.newHashMap();
        map.put("wxMpShareModelList", wxMpShareModelList);
        ContextHolderUtils.getRequest().setAttribute("map", map);

        return "wxmp/share/goIndex";
    }

    @PostMapping("/getUserAccountByUnionId")
    @ResponseBody
    public CommonJson getUserAccountByUnionId(@RequestParam String unionId) {



        CommonJson json = new CommonJson();
        return json;
    }
}
