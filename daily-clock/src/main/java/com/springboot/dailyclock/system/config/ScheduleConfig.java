package com.springboot.dailyclock.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * Created by weisy on 2018/5/15
 * 定时任务线程池设定
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //设定一个长度5的定时任务线程池
        scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(5));
    }
}
