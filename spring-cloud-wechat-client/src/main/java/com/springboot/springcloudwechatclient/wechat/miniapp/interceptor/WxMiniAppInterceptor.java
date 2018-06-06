package com.springboot.springcloudwechatclient.wechat.miniapp.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: spring-cloud-wechat-client
 * @description: 微信小程序权限拦截器
 * @author: weishiyao
 * @create: 2018-06-06 23:39
 **/
public class WxMiniAppInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(WxMiniAppInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
