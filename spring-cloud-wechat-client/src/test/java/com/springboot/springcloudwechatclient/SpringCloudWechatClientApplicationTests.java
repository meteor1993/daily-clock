package com.springboot.springcloudwechatclient;

import com.springboot.springcloudwechatclient.system.utils.Constant;
import org.joda.time.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCloudWechatClientApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        redisTemplate.delete(simpleDateFormat.format(new Date()) + "," + Constant.TODAY_SIGN_USER_LOG_0);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + System.currentTimeMillis() / 1000);
    }

    private String getOrderNo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = simpleDateFormat.format(new Date());
        return str + String.valueOf(System.nanoTime());
    }
}
