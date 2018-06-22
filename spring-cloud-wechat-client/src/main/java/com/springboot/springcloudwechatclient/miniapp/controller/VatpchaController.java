package com.springboot.springcloudwechatclient.miniapp.controller;

import com.springboot.springcloudwechatclient.miniapp.entity.VatpchaEntity;
import com.vaptcha.Vaptcha;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/miniapp/vatpcha")
public class VatpchaController {

    //验证单元ID
    String VID = "5b2b59e1a485e504101828df";
    // 验证单元密钥
    String KEY = "0d86a51f88b84e8e996364d05427ec0d";

    private Vaptcha vaptcha = new Vaptcha(VID, KEY);

    @PostMapping("/getVaptcha")
    public String getVaptcha(){
        String challenge = vaptcha.getChallenge(null);
        return challenge;
    }

    @PostMapping("/getDownTime")
    public String getDownTime(String data){
        String dowTime = vaptcha.downTime(data);
        return dowTime;
    }
    @PostMapping("/getDownTimeJsonP")
    public String getDownTimeJsonP(String callback, String data) {
        String dowTime = vaptcha.downTime(data);
        return String.format("%s(%s)", callback, dowTime);
    }

    /**
     * 获取用户的请求
     * @param entity(challenge,token)
     * 这里前端是通过formdata的数据发送的，所以接受参数的时候可以不用注解
     * 如果是payload里面需要用@requestBody的方式接收，写过springmvc都知道吧
     * @return
     */
    @PostMapping("/login")
    public String login(VatpchaEntity entity){
        Boolean status = vaptcha.validate(entity.getChallenge(),entity.getToken(),null);
        if(status){
            return "success";
        }else {
            return "faild";
        }

    }
}
