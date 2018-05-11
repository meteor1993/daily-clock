package com.springboot.dailyclock.wechat.mp.utils;


import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.system.utils.ContextHolderUtils;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import javax.servlet.http.HttpSession;

/**
 * Created by weishiyao on 2018/1/14.
 * 微信资源工具类
 */
public class ResourceWxMpUtil {

    /**
     * 根据sessionName获取信息
     * @param key
     * @return
     */
    public static final Object getCurrentSessionValueByName(String key){
        HttpSession session = ContextHolderUtils.getSession();
        return session.getAttribute(key);
    }
    /**
     * 设置当前session
     * @param key
     * @param obj
     */
    public static final void setCurrentSession(String key,Object obj){
        HttpSession session = ContextHolderUtils.getSession();
        session.setAttribute(key, obj);
    }

    /**
     * 根据session获取openId
     * @return
     */
    public static final String getSessionOpenId(){
        WxMpUser wxMpUser = (WxMpUser) getCurrentSessionValueByName(Constant.WX_MP_OPENID);
        if(wxMpUser != null) {
            return wxMpUser.getOpenId();
        }
        return "";
    }

    /**
     * 根据session获取wxUser
     * @return
     */
    public static WxMpUser getSessionWxMpUser(){
        WxMpUser wxMpUser = (WxMpUser) getCurrentSessionValueByName(Constant.WX_MP_USER);
        return wxMpUser;
    }

    /**
     * 移除当前session
     * @param key
     */
    public static final void removeCurrentSessionByName(String key){
        HttpSession session = ContextHolderUtils.getSession();
        session.removeAttribute(key);
    }

}
