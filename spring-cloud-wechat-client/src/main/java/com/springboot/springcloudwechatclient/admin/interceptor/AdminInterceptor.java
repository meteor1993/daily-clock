package com.springboot.springcloudwechatclient.admin.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.springboot.springcloudwechatclient.admin.model.AdminConfigUserModel;
import com.springboot.springcloudwechatclient.admin.remote.AdminRemote;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.StringUtil;
import com.springboot.springcloudwechatclient.wechat.mp.utils.ResourceWxMpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class AdminInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);

    @Autowired
    AdminRemote adminRemote;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String openid = ResourceWxMpUtil.getSessionOpenId();
        this.logger.info(">>>>>>>>>>>>>>>AdminInterceptor.preHandle>>>>>>>>>>>>>>>openid:" + openid);
        if (StringUtil.isNotEmpty(openid)) {
            CommonJson json = adminRemote.getAllAdminConfigUser();
            this.logger.info(">>>>>>>>>>>>>>>AdminInterceptor.preHandle>>>>>>>>>>>>>>>json:" + JSON.toJSONString(json));
            List<AdminConfigUserModel> list = JSONArray.parseArray(JSON.toJSONString(json.getResultData().get("list")), AdminConfigUserModel.class);
            this.logger.info(">>>>>>>>>>>>>>>AdminInterceptor.preHandle>>>>>>>>>>>>>>>list:" + JSON.toJSONString(list));
            for (AdminConfigUserModel userModel : list) {
                if (openid.equals(userModel.getOpenid())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
