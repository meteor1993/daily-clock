package com.springboot.dailyclock.task;

import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

/**
 * @program: daily-clock
 * @description: 汇总核算定时任务
 * @author: weishiyao
 * @create: 2018-05-18 00:04
 **/
@Configuration
@EnableScheduling
public class GatherTask {

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    /**
     * 核算定时任务，每天上午八点 启动
     */
    @Scheduled(cron="0 0 8 * * *")
    public void collectTask_0() {
        String no = "0";
        // 查询盘口0所有今日有资格打卡人
        List<String> openidList =  needClockUserDao.findAllNeedClockUserModel(new Date());

        // 循环今日所有有资格打卡人
        for (String str : openidList) {
            UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(str);



        }
    }
}
