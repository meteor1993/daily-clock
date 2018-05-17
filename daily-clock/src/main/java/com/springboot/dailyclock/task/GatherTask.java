package com.springboot.dailyclock.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @program: daily-clock
 * @description: 汇总核算定时任务
 * @author: weishiyao
 * @create: 2018-05-18 00:04
 **/
@Configuration
@EnableScheduling
public class GatherTask {

    /**
     * 核算定时任务，每天上午八点 启动
     */
    @Scheduled(cron="0 0 8 * * *")
    public void collectTask_0() {
        String no = "0";
        // 查询所有今日有资格打卡人

        // 对照打卡日志表和账户信息滚一圈

        // 计算总计金额
    }
}
