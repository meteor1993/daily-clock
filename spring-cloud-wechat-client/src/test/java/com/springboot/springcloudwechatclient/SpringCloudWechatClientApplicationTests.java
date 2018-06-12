package com.springboot.springcloudwechatclient;

import org.joda.time.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCloudWechatClientApplicationTests {

    @Test
    public void contextLoads() {
//        Period p = new Period(new DateTime(),new DateTime("2018-06-12 07:00:00"), PeriodType.hours());
        Duration duration = new Duration(new DateTime(), new DateTime(2018, 06, 12, 07, 00 ,00));
        Calendar c = Calendar.getInstance();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + Hours.hoursBetween(new DateTime(), new DateTime(2018, 06, 12, 07, 00 ,00)).getHours());
    }

    private String getOrderNo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = simpleDateFormat.format(new Date());
        return str + String.valueOf(System.nanoTime());
    }
}
