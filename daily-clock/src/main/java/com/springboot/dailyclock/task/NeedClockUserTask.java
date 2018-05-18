package com.springboot.dailyclock.task;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.pay.dao.WxPayOrderDao;
import com.springboot.dailyclock.pay.model.WxPayOrderModel;
import com.springboot.dailyclock.sign.dao.ClockConfigDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.ClockConfigModel;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.sign.model.UserClockLogModel;
import com.springboot.dailyclock.system.utils.Constant;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by weisy on 2018/5/15
 */
@Configuration
@EnableScheduling
public class NeedClockUserTask {

    @Autowired
    ClockConfigDao clockConfigDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    @Autowired
    UserClockLogDao userClockLogDao;

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    WxPayOrderDao wxPayOrderDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 生成可打卡列表定时任务，每天凌晨五点启动
     */
    @Scheduled(cron="0 0 5 * * *")
    public void needClockUserTask_0(){
        // 盘口号
        String no = "0";

        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs(no);

        // 查询所有开通盘口0的账户
        List<UserAccountModel> userAccountModelList = userAccountDao.findAllByType0("1");

        Map<String, Object> map = Maps.newHashMap();

        for (UserAccountModel model : userAccountModelList) {

            DateTime nowDate = new DateTime(new Date());
            DateTime orderDate = new DateTime(model.getOrderDate0());
            DateTime clockDate = new DateTime(model.getClockDate0());

            Period orderP = new Period(orderDate, nowDate, PeriodType.days());
            Period clockP = new Period(clockDate, nowDate, PeriodType.days());


            // 先判断是否打卡周期结束
            if ("".equals(model.getUseBalance0())) { // 当前打卡周期结束，今日无需打卡

            } else {
                // 再判断是否昨日充值
                if (orderP.getDays() == 1) { // 如果昨天充值，则今天可以打卡
                    commonClock(no, model.getOpenid(), map);
                } else {
                    // 再判断昨日是否正常打卡
                    if (clockP.getDays() == 1) { // 昨天正常打卡，今天可打卡
                        commonClock(no, model.getOpenid(), map);
                    } else { // 昨日未打卡，今日无需打卡

                    }
                }
            }
        }

        // 待签到数据放入缓存，4小时有效
        redisTemplate.opsForHash().putAll(Constant.TODAY_NEED_SIGN_USER_0, map);
        redisTemplate.expire(Constant.TODAY_NEED_SIGN_USER_0, 4, TimeUnit.HOURS); // 设定4小时过期

    }

    private void commonClock(String no, String openid, Map<String, Object> map) {
        NeedClockUserModel needClockUserModel = new NeedClockUserModel();
        needClockUserModel.setCreateDate(new Date());
        needClockUserModel.setNo(no);
        needClockUserModel.setOpenid(openid);
        map.put(openid, needClockUserModel);
        needClockUserDao.save(needClockUserModel);
    }
}
