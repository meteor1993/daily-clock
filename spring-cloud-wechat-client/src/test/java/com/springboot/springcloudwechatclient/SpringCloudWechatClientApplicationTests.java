package com.springboot.springcloudwechatclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCloudWechatClientApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + new Date(Long.valueOf("1528585200000" + "000")).toString());
    }

    private String getOrderNo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = simpleDateFormat.format(new Date());
        return str + String.valueOf(System.nanoTime());
    }
}
