package com.springboot.springcloudwechatclient.wechat.mp.interceptor;


import com.springboot.springcloudwechatclient.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by weishiyao on 2018/1/9.
 * 微信公众号拦截器
 */
public class WxMpInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WxMpInterceptor.class);

    private String queryString = "";
    private String requestAllPath = "";
    private String baseUrl = "";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info(">>>WxInterceptor>>>>>>>进入微信公众号拦截器>>>>>>>>>");
        setStrPath(request);
        if (request.getHeader("user-agent").indexOf(Constant.WECHAT_BROWSER) > 0) { // 是否微信浏览器
            if (request.getSession().getAttribute(Constant.WX_MP_OPENID) == null) { // 如果当前session中没有openid，则去取openid
                String oauth2scope = request.getParameter("oauth2scope");
                oauth2Dispath(response,requestAllPath,"wechatMpOuath/generOauthUrl?oauth2scope="+oauth2scope);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/wechatMpOuath/notwechatbrowser");
            return false;
        }

        return true;// 只有返回true才会继续向下执行，返回false取消当前请求
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * 设置请求路径
     * @param request
     */
    private void setStrPath(HttpServletRequest request) {
        if(request!=null){
            queryString = request.getQueryString() == null  ? "": ("?"+request.getQueryString());
            requestAllPath = (request.getRequestURI()  + queryString).substring(request.getContextPath().length() + 1);
            baseUrl =  request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";

        }
    }

    /**
     * 构造重定向参数
     * @param response
     * @param requestAllPath 原始全路径
     * @param dispathUrl 跳转路径
     * @throws IOException
     */
    private void oauth2Dispath(HttpServletResponse response, String requestAllPath, String dispathUrl) throws IOException{
        requestAllPath = Base64Utils.encodeToString(requestAllPath.getBytes("UTF-8"));
        String param = Constant.PREVIOUS_URL+"="+requestAllPath;
        response.sendRedirect(baseUrl+dispathUrl+"&"+param);
    }
}
