package com.springboot.dailyclock.task;

import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.admin.dao.AdminInfoDao;
import com.springboot.dailyclock.admin.dao.AdminMailAddressDao;
import com.springboot.dailyclock.admin.model.AdminInfoModel;
import com.springboot.dailyclock.admin.model.AdminMailAddressModel;
import com.springboot.dailyclock.sign.dao.ClockConfigDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.model.ClockConfigModel;
import com.springboot.dailyclock.system.mail.service.MailService;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

    @Autowired
    ClockConfigDao clockConfigDao;

    @Autowired
    MailService mailService;

    @Autowired
    AdminInfoDao adminInfoDao;

    @Autowired
    AdminMailAddressDao adminMailAddressDao;

    /**
     * 核算定时任务，每天上午八点 启动
     */
    @Scheduled(cron="0 0 8 * * *")
    public void collectTask_0() {
        String no = "0";
        // 查询盘口0所有今日有资格打卡人
        List<String> openidList =  needClockUserDao.findAllNeedClockUserModel(new Date());

        // 盘口0 当日所有押金
        String userBalance0Sum = needClockUserDao.getbalanceSumByNo(no, new Date());
        // 盘口0 当日所有打卡押金
        String userClockBalance0Sum = "";
        // 盘口0 当日所有未打卡押金
        String userUnClockBalance0Sum = "";
        // 盘口0 当日所有打卡人数
        int clockSum = 0;
        // 盘口0 当日所有未打卡人数
        int unClockSum = 0;

        DateTime end = new DateTime(new Date());

        BigDecimal bgClockBalance = new BigDecimal("0");

        BigDecimal bgUnClockBalance = new BigDecimal("0");

        // 循环今日所有有资格打卡人，统计总数据
        for (String openid : openidList) {
            UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);

            // 判断是否在打卡周期内 共两种情况，1在打卡周期内，2当日正好打卡周期结束
            if ("1".equals(userAccountModel.getType0())) { // 在打卡周期内
                // 判断是否当日打卡
                if (new Period(new DateTime(userAccountModel.getOrderDate0()), end, PeriodType.days()).getDays() == 0) { // 当日打卡
                    // 当日打卡人数+1
                    clockSum += 1;
                    // 当日打卡押金++
                    bgClockBalance = new BigDecimal(userAccountModel.getUseBalance0()).add(bgClockBalance);
                } else { // 当日未打卡
                    // 当日未打卡人数+1
                    unClockSum += 1;
                    // 当日未打卡押金++
                    bgUnClockBalance = new BigDecimal(userAccountModel.getUseBalance0()).add(bgUnClockBalance);
                    // 更新未打卡账户信息
                    userAccountModel.setType0("0");
                    // 修改账户押金
                    userAccountModel.setUseBalance0("");
                    // 修改最后打卡时间
                    userAccountModel.setClockDate0(null);
                    // 修改更新时间
                    userAccountModel.setUpdateDate(new Date());
                    userAccountDao.save(userAccountModel);
                }
            } else { // 当日打卡周期结束
                // 当日打卡人数+1
                clockSum += 1;
                // 当日打卡押金++
                bgClockBalance = new BigDecimal(userAccountModel.getUseBalance0()).add(bgClockBalance);
            }
        }
        // 数据汇总
        userClockBalance0Sum = bgClockBalance.toString();
        userUnClockBalance0Sum = bgUnClockBalance.toString();

        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs(no);

        // 除去抽成，剩余的奖金
        String str = bgUnClockBalance.subtract(new BigDecimal(clockConfigModel.getForMeAmount())).toString();

        // 除去保底，剩余可分配奖金
        String balance = new BigDecimal(str).subtract(new BigDecimal(clockSum).multiply(new BigDecimal(clockConfigModel.getBaodiAmount()))).toString();

        // 循环今日所有有资格打卡人，分配奖金
        for (String openid : openidList) {
            UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);
            // 判断是否在打卡周期内 共两种情况，1在打卡周期内，2当日正好打卡周期结束
            if ("1".equals(userAccountModel.getType0())) { // 在打卡周期内
                // 判断是否当日打卡
                if (new Period(new DateTime(userAccountModel.getOrderDate0()), end, PeriodType.days()).getDays() == 0) { // 当日打卡
                    // 今日应得奖金数，两位小数
                    BigDecimal todayBalance = new BigDecimal(userAccountModel.getUseBalance0()).divide(bgClockBalance).multiply(new BigDecimal(balance)).setScale(2, BigDecimal.ROUND_DOWN);
                    // 每日新增奖金赋值
                    userAccountModel.setTodayBalance0(todayBalance.add(new BigDecimal(clockConfigModel.getBaodiAmount())).setScale(2, BigDecimal.ROUND_DOWN).toString());
                    // 加上保底，加上原有奖金，两位小数
                    userAccountModel.setBalance(todayBalance.add(new BigDecimal(clockConfigModel.getBaodiAmount())).add(new BigDecimal(userAccountModel.getBalance())).setScale(2, BigDecimal.ROUND_DOWN).toString());

                }
            } else { // 当日打卡周期结束
                // 今日应得奖金数，两位小数
                BigDecimal todayBalance = new BigDecimal(userAccountModel.getUseBalance0()).divide(bgClockBalance).multiply(new BigDecimal(balance)).setScale(2, BigDecimal.ROUND_DOWN);
                // 每日新增奖金赋值
                userAccountModel.setTodayBalance0(todayBalance.add(new BigDecimal(clockConfigModel.getBaodiAmount())).setScale(2, BigDecimal.ROUND_DOWN).toString());
                // 加上保底，加上原有奖金，两位小数
                userAccountModel.setBalance(todayBalance.add(new BigDecimal(clockConfigModel.getBaodiAmount())).add(new BigDecimal(userAccountModel.getBalance())).setScale(2, BigDecimal.ROUND_DOWN).toString());
            }
        }
        // 汇总数据保存至数据库
        AdminInfoModel adminInfoModel = new AdminInfoModel();
        adminInfoModel.setCreateDate(new Date());
        adminInfoModel.setForMeAmount(clockConfigModel.getForMeAmount());
        adminInfoModel.setNo(no);
        adminInfoModel.setSumClockAmount(userClockBalance0Sum);
        adminInfoModel.setSumClockUser(String.valueOf(clockSum));
        adminInfoModel.setSumUnClockAmount(userUnClockBalance0Sum);
        adminInfoModel.setSumUnClockUser(String.valueOf(unClockSum));
        adminInfoDao.save(adminInfoModel);

        // 封装邮件数据，发送邮件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String subject = sdf.format(new Date()) + "数据汇总";
        String content = "今日打卡总人数：" + String.valueOf(clockSum) + "，未打卡总人数：" + String.valueOf(unClockSum) + "，打卡总金额：" + userClockBalance0Sum + "，未打卡总金额：" + userUnClockBalance0Sum + "，抽水金额：" + clockConfigModel.getForMeAmount();
        List<AdminMailAddressModel> adminMailAddressModelList = adminMailAddressDao.findAllByType("1");
        for (AdminMailAddressModel model : adminMailAddressModelList) {
            mailService.sendSimpleMail(model.getMailAddress(), subject, content);
        }
    }
}






















