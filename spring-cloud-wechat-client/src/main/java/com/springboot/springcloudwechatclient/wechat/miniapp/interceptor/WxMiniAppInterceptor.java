package com.springboot.springcloudwechatclient.wechat.miniapp.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @program: spring-cloud-wechat-client
 * @description: 微信小程序权限拦截器
 * @author: weishiyao
 * @create: 2018-06-06 23:39
 **/
public class WxMiniAppInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(WxMiniAppInterceptor.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");

        this.logger.info(">>>>>>>>>>>>>>>>>token:" + token);

        Map<Object, Object> map = redisTemplate.opsForHash().entries(token);

        if (map == null) { // 判断当前缓存是否存在token

            PrintWriter out = response.getWriter();

            out.print("{\"resultCode\":\"-1\",\"resultMessage\":\"No authority\",\"resultData\":\"[{}]\"}");

            return false;
        } else {

            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
