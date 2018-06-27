package com.springboot.dailyclock.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.dao.UserAccountLogDao;
import com.springboot.dailyclock.account.model.UserAccountLogModel;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.admin.dao.AdminInfoDao;
import com.springboot.dailyclock.admin.model.AdminInfoModel;
import com.springboot.dailyclock.admin.service.AdminService;
import com.springboot.dailyclock.sign.dao.ClockConfigDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.model.ClockConfigModel;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service(value = "adminService")
public class AdminServiceI implements AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminServiceI.class);

    @Autowired
    ClockConfigDao clockConfigDao;

    @Autowired
    AdminInfoDao adminInfoDao;

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    UserAccountLogDao userAccountLogDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    @Transactional(timeout = 3600)
    public CommonJson gatherData() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // 获取盘口配置
        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs("0");

        // 获取今日抽水配置
        AdminInfoModel adminInfoModel = adminInfoDao.getAdminInfoModelByCreateDate(simpleDateFormat.format(new Date()));

        // 当日已打卡总金额
        String clockUserBalance0Sum = userAccountDao.getClockUserBalance0Sum(new Date());

        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>clockUserBalance0Sum:" + clockUserBalance0Sum);

        /**
         * 获取所有有资格打卡的人循环
         * 生成今日打卡列表和今日未打卡列表
         * 对于今日未打卡人，打卡状态改变，押金增加至奖池，账户押金归零，账户变动log记录
         * 对于今日已打卡人，打卡状态不变，分奖金（奖池-抽水-保底 按照份额分配 + 保底）生成第二日打卡资格，存入数据库，账户变动log记录
         * 今日打卡满21日，分奖金，押金进余额，是否还有押金剩余，有剩余生成第二日打卡资格
         */
        // 总奖池
        BigDecimal amountSumBg = new BigDecimal("0");

        // 当日盘口0所有未打卡用户列表
        List<UserAccountModel> unClockUserList = userAccountDao.findAllUnClockUser(new Date());
        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>unClockUserList:" + unClockUserList);
        // 当日盘口0所有已打卡用户列表
        List<UserAccountModel> clockUserList = userAccountDao.findAllClockUser(new Date());
        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>clockUserList:" + clockUserList);

        // 循环所有未打卡用户列表
        for (UserAccountModel userAccountModel : unClockUserList) {
            userAccountModel.setType0(null);
            userAccountModel.setOrderDate0(null);
            userAccountModel.setContinuousClockNum(null);
            userAccountModel.setClockDate0(null);
            // 增加总奖池奖金
            amountSumBg = amountSumBg.add(new BigDecimal(userAccountModel.getUseBalance0()));

            // 账户变动log
            UserAccountLogModel userAccountLogModel = new UserAccountLogModel();
            userAccountLogModel.setAmount(userAccountModel.getUseBalance0());
            userAccountLogModel.setNo("0");
            userAccountLogModel.setCreateDate(new Date());
            userAccountLogModel.setOpenid(userAccountModel.getOpenid());
            userAccountLogModel.setType("6");

            // 账户押金归零
            userAccountModel.setUseBalance0(null);
            userAccountModel.setRewardBalance(null);

            userAccountDao.save(userAccountModel);
            userAccountLogDao.save(userAccountLogModel);
        }

        amountSumBg = amountSumBg.add(new BigDecimal(clockConfigModel.getSubsidy() == null ? "0" : clockConfigModel.getSubsidy()));

        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>amountSum:" + amountSumBg.toString());

        // 保底额度
        BigDecimal baodiBig = new BigDecimal(clockConfigModel.getBaodiAmount()).multiply(new BigDecimal(clockUserList.size()));

        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>baodiBig:" + baodiBig.toString());

        // 待分配额度
        BigDecimal daiBig = amountSumBg.subtract(new BigDecimal(adminInfoModel.getForMeAmount())).subtract(baodiBig);

        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>daiBig:" + daiBig.toString());

        // 循环所有已打卡用户列表
        // 对于今日已打卡人，打卡状态不变，分奖金（奖池-抽水-保底 按照份额分配 + 保底）生成第二日打卡资格，存入数据库，账户变动log记录
        // 今日打卡满21日，分奖金，押金进余额，是否还有押金剩余，有剩余生成第二日打卡资格
        for (UserAccountModel userAccountModel : clockUserList) {
            this.logger.info(">>>>>>>>>>>>>>>>>>>>>>>>userAccountModel.getOpenid():" + userAccountModel.getOpenid());
            List<UserAccountLogModel> userAccountLogModelList = userAccountLogDao.findAllByOpenidAndTypeAndNoAndTypeFlagOrderByCreateDateAsc(userAccountModel.getOpenid(), "1", "0", "1");

            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>userAccountLogModelList:" + userAccountLogModelList.toString());

            List<UserAccountLogModel> userAccountLogModelListCopy = new ArrayList<>();
            for (UserAccountLogModel model : userAccountLogModelList) {
                if (simpleDateFormat.format(userAccountLogModelList.get(0).getCreateDate()).equals(simpleDateFormat.format(model.getCreateDate()))) {
                    userAccountLogModelListCopy.add(model);
                }
            }
//            UserAccountLogModel userAccountLogModel = userAccountLogModelList.get(0);

            // 判断是否满21天
            int days = Days.daysBetween(new DateTime(userAccountLogModelListCopy.get(0).getCreateDate()), new DateTime(new Date())).getDays();
            // 需要扣钱的总押金
            BigDecimal bgAmount = new BigDecimal("0");
            for (UserAccountLogModel model : userAccountLogModelListCopy) {
                bgAmount = bgAmount.add(new BigDecimal(model.getAmount()));
            }

            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>days1:" +days);

            // 剩余押金
            Double d = new BigDecimal(userAccountModel.getUseBalance0()).subtract(bgAmount).doubleValue();

//            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>compTime:" +compTime(clockConfigModel.getClockStartTime(), sdf.format(userAccountLogModelListCopy.get(0).getCreateDate())));
            if (compTime(clockConfigModel.getClockStartTime(), sdf.format(userAccountLogModelListCopy.get(0).getCreateDate()))) {
                days += 1;
            }

            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>days2:" +days);

            // 分奖金
            // 按份额分配 个人押金 / 总押金 * 待分配额度
            // 个人额度
            BigDecimal personAmount = new BigDecimal(userAccountModel.getUseBalance0()).add(new BigDecimal(userAccountModel.getRewardBalance() == null ? "0" : userAccountModel.getRewardBalance()));
            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>personAmount:" + personAmount.toString());
            // 当前占比
            BigDecimal personScale = personAmount.divide(new BigDecimal(clockUserBalance0Sum), 8, BigDecimal.ROUND_HALF_UP);
            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>personScale:" + personScale.toString());
            // 最终分钱额度
            BigDecimal zuizhongAmount = personScale.multiply(daiBig).setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(clockConfigModel.getBaodiAmount()));
            this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>zuizhongAmount:" + zuizhongAmount.toString());

            // 个人账户赋值
            userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(zuizhongAmount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            // 当日奖金赋值
            userAccountModel.setTodayBalance0(zuizhongAmount.toString());
            // 总奖金额度赋值
            userAccountModel.setBalanceSum0(new BigDecimal(userAccountModel.getBalanceSum0() == null ? "0" : userAccountModel.getBalanceSum0()).add(zuizhongAmount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            // 增加账户变动log 奖金发放
            UserAccountLogModel userAccountLogModel2 = new UserAccountLogModel();
            userAccountLogModel2.setAmount(zuizhongAmount.toString());
            userAccountLogModel2.setNo("0");
            userAccountLogModel2.setCreateDate(new Date());
            userAccountLogModel2.setOpenid(userAccountModel.getOpenid());
            userAccountLogModel2.setType("2");
            userAccountLogDao.save(userAccountLogModel2);

            // 当前时间满21天 押金进余额 是否还有押金剩余，有剩余生成第二日打卡资格
            if (days >= clockConfigModel.getClockTime()) {

                // 更新用户账户信息
                userAccountModel.setBalance(bgAmount.add(new BigDecimal(userAccountModel.getBalance())).toString());

                if (d > 0) { // 如果押金还有剩余
                    // 生成第二天打卡资格
                    createClockUser(userAccountModel.getOpenid(), userAccountModel.getUseBalance0());
                } else { // 押金无剩余
                    // 修改账户信息
                    userAccountModel.setUseBalance0(null);
                    userAccountModel.setType0(null);
                    userAccountModel.setClockDate0(null);
                    userAccountModel.setContinuousClockNum(null);
                    userAccountModel.setOrderDate0(null);
                    // 奖励金清零
                    userAccountModel.setRewardBalance(null);
                }
                // 增加账户变动log，押金付款到余额
                UserAccountLogModel userAccountLogModel1 = new UserAccountLogModel();
                userAccountLogModel1.setAmount(bgAmount.toString());
                userAccountLogModel1.setNo("0");
                userAccountLogModel1.setCreateDate(new Date());
                userAccountLogModel1.setOpenid(userAccountModel.getOpenid());
                userAccountLogModel1.setType("5");

                // log保存至数据库
                // 修改该条log无效
                for (UserAccountLogModel model : userAccountLogModelListCopy) {
                    model.setTypeFlag("0");
                    userAccountLogDao.save(model);
                }
                userAccountLogDao.save(userAccountLogModel1);

            } else { // 时间未满21天，正常处理数据
                createClockUser(userAccountModel.getOpenid(), userAccountModel.getUseBalance0());
            }
        }
        CommonJson json = new CommonJson();
        Map<String, Object> map = Maps.newHashMap();
        map.put("baodiBig", baodiBig.toString());
        map.put("daiBig", daiBig.toString());
        map.put("amountSumBg", amountSumBg.toString());
        map.put("clockUser", clockUserList.size());
        map.put("unClockUser", unClockUserList.size());
        json.setResultData(map);
        return json;
    }

    @Transactional(timeout = 3600)
    public void createClockUser(String openid, String useBalance) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        NeedClockUserModel needClockUserModel = needClockUserDao.getByOpenidAndNeedDate(openid, sdf.format(new DateTime().plusDays(1).toDate()));
        if (needClockUserModel == null) {
            needClockUserModel = new NeedClockUserModel();
            needClockUserModel.setCreateDate(new Date());
            needClockUserModel.setNo("0");
            needClockUserModel.setNeedDate(new DateTime(new Date()).plusDays(1).toDate());
        }
        needClockUserModel.setUseBalance(useBalance);
        needClockUserDao.save(needClockUserModel);

        // 设置缓存

        String TODAY_NEED_SIGN_USER = sdf.format(new DateTime().plusDays(1).toDate()) + "," + Constant.TODAY_NEED_SIGN_USER_0;

        boolean hasNeedSign = redisTemplate.hasKey(TODAY_NEED_SIGN_USER);
        if (hasNeedSign) {
            redisTemplate.opsForHash().put(TODAY_NEED_SIGN_USER, openid, JSON.toJSONString(needClockUserModel));
        } else {
            List<NeedClockUserModel> needClockUserModelList = needClockUserDao.findAllByNeedDateBetweenAndNo(simpleDateFormat.parse(sdf.format(new Date()) + " 00:00:00"), simpleDateFormat.parse(sdf.format(new Date()) + " 23:59:59"), "0");
            Map<String, Object> map = Maps.newHashMap();
            for (NeedClockUserModel model : needClockUserModelList) {
                map.put(model.getOpenid(), JSON.toJSONString(model));
            }
            redisTemplate.opsForHash().putAll(TODAY_NEED_SIGN_USER, map);
        }

        // 设定24小时过期
        redisTemplate.expire(TODAY_NEED_SIGN_USER, 24, TimeUnit.HOURS);

    }

    private boolean compTime(String s1,String s2){
        this.logger.info(">>>>>>>>>>>>>>>AdminServiceI.compTime>>>>>>>>>>>s1:" + s1 + ">>>>>>>>>>>s2:" + s2);
        try {
            if (s1.indexOf(":") < 0 || s1.indexOf(":") < 0) {
                this.logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>格式不正确");
            }else{
                String[] array1 = s1.split(":");
                int total1 = Integer.valueOf(array1[0]) * 3600 + Integer.valueOf(array1[1]) * 60 + Integer.valueOf(array1[2]);
                this.logger.info(">>>>>>>>>>>>>>>AdminServiceI.compTime>>>>>>>>>>>total1:" + total1);
                String[] array2 = s2.split(":");
                int total2 = Integer.valueOf(array2[0]) * 3600 + Integer.valueOf(array2[1]) * 60 + Integer.valueOf(array2[2]);
                this.logger.info(">>>>>>>>>>>>>>>AdminServiceI.compTime>>>>>>>>>>>total2:" + total2);
                return total1 - total2 > 0 ? true : false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            return true;
        }
        return false;

    }
}
