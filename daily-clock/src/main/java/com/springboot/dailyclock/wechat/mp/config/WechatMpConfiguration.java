package com.springboot.dailyclock.wechat.mp.config;

import com.springboot.dailyclock.wechat.mp.handler.*;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by weishiyao on 2018/1/24.
 */
@Configuration
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WechatMpProperties.class)
public class WechatMpConfiguration {
    @Autowired
    protected NullHandler nullHandler;
    @Autowired
    protected KfSessionHandler kfSessionHandler;
    @Autowired
    protected StoreCheckNotifyHandler storeCheckNotifyHandler;
    @Autowired
    private WechatMpProperties properties;
    @Autowired
    private MenuHandler menuHandler;
    @Autowired
    private UnsubscribeHandler unsubscribeHandler;

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage configStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(this.properties.getAppId());
        configStorage.setSecret(this.properties.getSecret());
        configStorage.setToken(this.properties.getToken());
        configStorage.setAesKey(this.properties.getAesKey());
        return configStorage;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    protected MenuHandler getMenuHandler() {
        return this.menuHandler;
    }

    protected UnsubscribeHandler getUnsubscribeHandler() {
        return this.unsubscribeHandler;
    }

    protected AbstractHandler getScanHandler() {
        return null;
    }

}
