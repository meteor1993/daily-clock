package com.springboot.springcloudwechatclient.system.config;

import com.springboot.springcloudwechatclient.admin.interceptor.AdminInterceptor;
import com.springboot.springcloudwechatclient.system.interceptor.WebInterceptor;
import com.springboot.springcloudwechatclient.wechat.miniapp.interceptor.WxMiniAppInterceptor;
import com.springboot.springcloudwechatclient.wechat.mp.interceptor.WxMpInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Created by weishiyao on 2018/1/9.
 * web配置类
 */
@Configuration
public class WebAppConfigurer extends WebMvcConfigurationSupport {

    @Bean
    WxMiniAppInterceptor wxMiniAppInterceptor() {
        return new WxMiniAppInterceptor();
    }

    @Bean
    AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*
         * 多个拦截器组成一个拦截器链
         * addPathPatterns 用于添加拦截规则
         * excludePathPatterns 用户排除拦截
         */

        // 全局拦截方法
        registry.addInterceptor(new WebInterceptor()).addPathPatterns("/**");

        // 微信公众号拦截器
        registry.addInterceptor(new WxMpInterceptor()).addPathPatterns("/wxMp/**").excludePathPatterns("/wxMp/notwechatbrowser");

        // 微信公众号后台管理员拦截器
        registry.addInterceptor(adminInterceptor()).addPathPatterns("/wxMp/admin/**");

        // 微信小程序拦截器
        registry.addInterceptor(wxMiniAppInterceptor()).addPathPatterns("/miniapp/**").excludePathPatterns("/miniapp/getToken");

        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX);
        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
        super.addResourceHandlers(registry);
    }

}

