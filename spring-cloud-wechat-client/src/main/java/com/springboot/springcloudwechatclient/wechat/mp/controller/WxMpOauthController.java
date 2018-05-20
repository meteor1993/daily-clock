package com.springboot.springcloudwechatclient.wechat.mp.controller;

import com.google.common.collect.Maps;
import com.springboot.springcloudwechatclient.system.model.CommonJson;
import com.springboot.springcloudwechatclient.system.utils.Constant;
import com.springboot.springcloudwechatclient.system.utils.StringUtil;
import com.springboot.springcloudwechatclient.wechat.mp.utils.ResourceWxMpUtil;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by weishiyao on 2018/1/24.
 * 微信公众号系统controller
 */
@Controller
@RequestMapping(value = "/wechatMpOuath")
public class WxMpOauthController {

    private static final Logger logger = LoggerFactory.getLogger(WxMpOauthController.class);

    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/notwechatbrowser")
    public String notwechatbrowser(ModelMap map) {
        map.put("msg", "请在微信浏览器打开该页面");
        return "wxmp/notwechatbrowser";
    }

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WxMpConfigStorage wxMpConfigStorage;

//    private WxMpUtil wxMpUtil = new WxMpUtil();
    private WxMpUser wxMpUser = null;

    /**
     * 网页授权方式 默认使用非静默方式
     * @param oauth2scope
     * @return
     */
    private String getOauth2scope(String oauth2scope){
        if(WxConsts.OAuth2Scope.SNSAPI_BASE.equals(oauth2scope)) {
            oauth2scope = WxConsts.OAuth2Scope.SNSAPI_BASE;
        } else {
            oauth2scope = WxConsts.OAuth2Scope.SNSAPI_USERINFO;
        }
        return oauth2scope;
    }

    /**
     * 微信授权
     * @param request
     * @return
     */
    @RequestMapping(value = "/generOauthUrl")
    public String generOauthUrl(HttpServletRequest request) {

        String oauthUrl = "";//授权访问链接
        String previousURL = request.getParameter(Constant.PREVIOUS_URL);//原始访问地址
        String appUtilParam = request.getParameter("appUtilParam");
        String basePath = request.getScheme()+"://"+request.getServerName()+request.getContextPath()+"/";;

        /**回调地址*/
        String callBackURL = basePath+"/wechatMpOuath/callbackRequest?previousURL="+previousURL;
        /** 获取用户信息方式 默认使用非静默方式 */
        String oauth2scope = request.getParameter("oauth2scope");
        oauth2scope = getOauth2scope(oauth2scope);
        oauthUrl = wxMpService.oauth2buildAuthorizationUrl(callBackURL, oauth2scope,Math.random()+"");

        return "redirect:" + oauthUrl;
    }

    /**
     * 微信授权回调
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/callbackRequest")
    public String callbackRequest(HttpServletRequest request) throws UnsupportedEncodingException {
        String code = request.getParameter("code");
        String previousURL = request.getParameter("previousURL");
        logger.info("-----------code:"+code+",previousURL:"+previousURL);
        try {
            previousURL = StringUtil.isNotEmpty(previousURL) ? new String(Base64Utils.decodeFromString(previousURL), "UTF-8"): "";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("------------"+"获取原有地址解析失败"+previousURL);
        }

        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
        } catch (WxErrorException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            // 跳回重新获取路径
            return "redirect:/wechatMpOuath/generOauthUrl?" + Constant.PREVIOUS_URL + "=" + Base64Utils.encodeToString(previousURL.getBytes("UTF-8"));
        }

        if (wxMpUser != null) {
            // 缓存信息
            ResourceWxMpUtil.setCurrentSession(Constant.WX_MP_USER, wxMpUser);
            ResourceWxMpUtil.setCurrentSession(Constant.WX_MP_OPENID, wxMpUser.getOpenId());
            logger.info("-----------wxMpUser:"+wxMpUser+",state:"+previousURL);
        }

        return "redirect:/" + previousURL;
    }

    /**
     * 生成jsdk签名
     * @return
     */
    @RequestMapping(value = "ajaxJsapiSignature")
    @ResponseBody
    public CommonJson ajaxJsapiSignature(String locaHref) {
        CommonJson j = new CommonJson();
        WxJsapiSignature jsapiSignature = null;
//        WxMpUtil wxMpUtil = new WxMpUtil();
        String appId = "";
        try {
            appId = wxMpConfigStorage.getAppId();
            jsapiSignature = wxMpService.createJsapiSignature(locaHref);
            logger.info("----------ajaxJsapiSignature:"+ locaHref);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("appId", appId);
        map.put("jsapiSignature", jsapiSignature);
        j.setResultData(map);
        return j;
    }
}
