package com.springboot.dailyclock.system.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weisy on 2018/4/18
 * CORS过滤器
 */
@Configuration
public class CorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    private String allowOrigin;

    private String allowMethods;

    private String allowCredentials;

    private String allowHeaders;

    private String exposeHeaders;

    // 需要注意的是，CORS规范中定义Access-Control-Allow-Origin只允许两种取值，要么为*，要么为具体的域名，
    // 也就是说，不支持同时配置多个域名。为了解决跨多个域的问题，需要在代码中做一些处理，这里将Filter初始化参数作为一个域名的集合（用逗号分隔），
    // 只需从当前请求中获取Origin请求头，就知道是从哪个域中发出的请求，若该请求在以上允许的域名集合中，则将其放入Access-Control-Allow-Origin响应头，这样跨多个域的问题就轻松解决了。
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        // 允许访问的客户端域名，例如：http://web.xxx.com，若为*，则表示从任意域都能访问，即不做任何限制。
        allowOrigin = filterConfig.getInitParameter("allowOrigin");

        // 允许访问的方法名，多个方法名用逗号分割，例如：GET,POST,PUT,DELETE,OPTIONS。
        allowMethods = filterConfig.getInitParameter("allowMethods");

        // 是否允许请求带有验证信息，若要获取客户端域下的cookie时，需要将其设置为true。
        allowCredentials = filterConfig.getInitParameter("allowCredentials");

        // 允许服务端访问的客户端请求头，多个请求头用逗号分割，例如：Content-Type。
        allowHeaders = filterConfig.getInitParameter("allowHeaders");

        // 允许客户端访问的服务端响应头，多个响应头用逗号分割。
        exposeHeaders = filterConfig.getInitParameter("exposeHeaders");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>CORS进入预检请求，开始处理预检>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        logger.debug(">>>>>>>>>allowOrigin:" + allowOrigin + ">>>>>>>>allowMethods:" + allowMethods + ">>>>>>>>allowCredentials:" + allowCredentials + ">>>>>>>>>>>allowHeaders:" + allowHeaders + ">>>>>>>exposeHeaders:" + exposeHeaders);
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!StringUtils.isEmpty(allowOrigin)) {

            List<String> allowOriginList = Arrays.asList(allowOrigin.split(","));

            if (allowOriginList.size() > 0) {

                String currentOrigin = request.getHeader("Origin");

                if (allowOriginList.contains(currentOrigin)) {

                    response.setHeader("Access-Control-Allow-Origin", currentOrigin);

                }

            }

        } else {
            response.setHeader("Access-Control-Allow-Origin", "http://webservicedev.efesco.com");
        }

        if (!StringUtils.isEmpty(allowMethods)) {

            response.setHeader("Access-Control-Allow-Methods", allowMethods);

        }

        if (!StringUtils.isEmpty(allowCredentials)) {

            response.setHeader("Access-Control-Allow-Credentials", allowCredentials);

        }

        if (!StringUtils.isEmpty(allowHeaders)) {

            response.setHeader("Access-Control-Allow-Headers", allowHeaders);

        }

        if (!StringUtils.isEmpty(exposeHeaders)) {

            response.setHeader("Access-Control-Expose-Headers", exposeHeaders);

        }

        response.setHeader("Access-Control-Allow-Credentials", "true");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
