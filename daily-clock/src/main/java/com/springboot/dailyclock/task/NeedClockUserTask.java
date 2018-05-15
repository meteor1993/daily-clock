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
     * 默认活动定时任务，每天凌晨五点启动
     */
    @Scheduled(cron="0 0 5 * * *")
    public void needClockUserTask_0(){
        // 盘口号
        String no = "0";

        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs(no);

        // 查询所有开通盘口0的账户
        List<UserAccountModel> userAccountModelList = userAccountDao.findAllByType0(no);

        Map<String, Object> map = Maps.newHashMap();

        for (UserAccountModel model : userAccountModelList) {




            // 查询当前人员所有打卡记录
            List<UserClockLogModel> userClockLogModelList = userClockLogDao.findAllByOpenIdOrderByCreateDateDesc(model.getOpenid());
            List<WxPayOrderModel> wxPayOrderModelList = wxPayOrderDao.findAllByOpenIdAndOrderStatusOrderByOrderTimeDesc(model.getOpenid(), Constant.SUCCESS_CODE);
            DateTime dateTime = new DateTime(new Date());
            String yesterday = dateTime.minusDays(1).toString("yyyy-MM-dd");
            String orderTime = new DateTime(wxPayOrderModelList.get(0).getOrderTime()).toString("yyyy-MM-dd");
            if (userClockLogModelList.size() == 0){ // 如果当前人员没有打卡记录，去查询充值记录是否为昨日
                if (yesterday.equals(orderTime)) { // 如果充值记录是昨天，则可以今日打卡
                    NeedClockUserModel needClockUserModel = new NeedClockUserModel();
                    needClockUserModel.setCreateDate(new Date());
                    needClockUserModel.setNo(no);
                    needClockUserModel.setOpenid(model.getOpenid());
                    map.put(model.getOpenid(), needClockUserModel);
                    needClockUserDao.save(needClockUserModel);
                }
            } else { // 如果有打卡记录
                // 对比是否完成打卡周期
                DateTime begin = new DateTime(userClockLogModelList.get(0).getCreateDate());
                DateTime end = new DateTime(userClockLogModelList.get(userClockLogModelList.size() - 1).getCreateDate());
                Period p = new Period(begin, end, PeriodType.days());
                int days = p.getDays();
                if (clockConfigModel.getClockTime() % days == 0) { // 先判断打卡次数是否等于一个或者多个周期
                    // 如果正好为一个周期，则当日无需打卡
                } else {
                    // 如果是不是一个周期，则判断昨日是否是充值日
                    if (yesterday.equals(orderTime)) { // 如果充值记录是昨天，则可以今日打卡
                        NeedClockUserModel needClockUserModel = new NeedClockUserModel();
                        needClockUserModel.setCreateDate(new Date());
                        needClockUserModel.setNo(no);
                        needClockUserModel.setOpenid(model.getOpenid());
                        map.put(model.getOpenid(), needClockUserModel);
                        needClockUserDao.save(needClockUserModel);
                    } else {
                        // 如果充值记录不是昨日，则判断昨日是否打卡
                        String lastClockTime = new DateTime(userClockLogModelList.get(0).getCreateDate()).toString("yyyy-MM-dd");
                        if (yesterday.equals(lastClockTime)) { // 上次打卡是昨日，则可以今日打卡
                            NeedClockUserModel needClockUserModel = new NeedClockUserModel();
                            needClockUserModel.setCreateDate(new Date());
                            needClockUserModel.setNo(no);
                            needClockUserModel.setOpenid(model.getOpenid());
                            map.put(model.getOpenid(), needClockUserModel);
                            needClockUserDao.save(needClockUserModel);
                        }
                    }
                }
            }
        }

        // 待签到数据放入缓存，4小时有效
        redisTemplate.opsForHash().putAll(Constant.TODAY_NEED_SIGN_USER_0, map);
        redisTemplate.expire(Constant.TODAY_NEED_SIGN_USER_0, 4, TimeUnit.HOURS); // 设定4小时过期

    }
}
