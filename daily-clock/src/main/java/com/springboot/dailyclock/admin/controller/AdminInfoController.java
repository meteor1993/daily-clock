package com.springboot.dailyclock.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.dao.UserAccountLogDao;
import com.springboot.dailyclock.account.model.UserAccountLogModel;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.admin.dao.AdminInfoDao;
import com.springboot.dailyclock.admin.model.AdminInfoModel;
import com.springboot.dailyclock.sign.dao.ClockConfigDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.ClockConfigModel;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by weisy on 2018/5/22
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminInfoController {

    private final Logger logger = LoggerFactory.getLogger(AdminInfoController.class);

    @Autowired
    AdminInfoDao adminInfoDao;

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    UserAccountLogDao userAccountLogDao;

    @Autowired
    ClockConfigDao clockConfigDao;

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 根据创建日期得到AdminInfo
     * @param date
     * @return
     */
    @PostMapping(value = "/getAdminInfoByCreateDate")
    public CommonJson getAdminInfoByCreateDate(@RequestParam String date) {
        this.logger.info(">>>>>>>>>>>>>>>>>>>>AdminInfoController.getAdminInfoByCreateDate>>>>>>>>>>>>>>>>>");
        CommonJson json = new CommonJson();
        AdminInfoModel adminInfoModel = adminInfoDao.getAdminInfoModelByCreateDate(date);
        Map<String, Object> map = Maps.newHashMap();
        map.put("adminInfoModel", adminInfoModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 保存/更新 adminInfoModel
     * @param adminInfoModel
     * @return
     */
    @PostMapping(value = "/saveAdminInfo")
    public CommonJson saveAdminInfo(@RequestBody AdminInfoModel adminInfoModel) {
        this.logger.info(">>>>>>>>>>>>>>>>>>>>AdminInfoController.saveAdminInfo>>>>>>>>>>>>>>>>>");
        CommonJson json = new CommonJson();
        AdminInfoModel adminInfoModel1 = adminInfoDao.save(adminInfoModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put("adminInfoModel", adminInfoModel1);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 管理员数据查询
     * @return
     */
    @PostMapping(value = "/getDataInfo")
    public CommonJson getDataInfo() {
        CommonJson json = new CommonJson();
        // 已打卡总金额
        String clockUserBalance0Sum = userAccountDao.getClockUserBalance0Sum(new Date());
        // 未打卡总金额
        String unClockUserBalance0Sum = userAccountDao.getUnClockUserBalance0Sum(new Date());
        // 已打卡总人数
        String clockUserCount0 = userAccountDao.getClockUserCount0(new Date());
        // 未打卡总人数
        String unClockUserCount0 = userAccountDao.getUnClockUserCount0(new Date());
        Map<String, Object> map = Maps.newHashMap();
        map.put("clockUserBalance0Sum", clockUserBalance0Sum);
        map.put("unClockUserBalance0Sum", unClockUserBalance0Sum);
        map.put("clockUserCount0", clockUserCount0);
        map.put("unClockUserCount0", unClockUserCount0);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 根据手机号查询账户信息
     * @param mobile
     * @return
     */
    @PostMapping(value = "/getUserByMobile")
    public CommonJson getUserByMobile(@RequestParam String mobile) {
        CommonJson json = new CommonJson();
        WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByMobileIs(mobile);
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(wechatMpUserModel.getWechatOpenId());
        Map<String, Object> map = Maps.newHashMap();
        map.put("wechatMpUserModel", wechatMpUserModel);
        map.put("userAccountModel", userAccountModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    /**
     * 每日数据结算
     * @return
     * @throws ParseException
     */
    @PostMapping(value = "/gatherData")
    public CommonJson gatherData() throws ParseException {
        CommonJson json = new CommonJson();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 获取盘口配置
        ClockConfigModel clockConfigModel = clockConfigDao.getByIdIs("0");

        // 获取今日抽水配置
        AdminInfoModel adminInfoModel = adminInfoDao.getAdminInfoModelByCreateDate(simpleDateFormat.format(new Date()));

        // 当日已打卡总金额
        String clockUserBalance0Sum = userAccountDao.getClockUserBalance0Sum(new Date());


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
        // 当日盘口0所有已打卡用户列表
        List<UserAccountModel> clockUserList = userAccountDao.findAllClockUser(new Date());

        // 循环所有未打卡用户列表
        for (UserAccountModel userAccountModel : unClockUserList) {
            userAccountModel.setType0(null);
            userAccountModel.setOrderDate0(null);
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

            userAccountDao.save(userAccountModel);
            userAccountLogDao.save(userAccountLogModel);
        }

        this.logger.info(">>>>>>>>>>>>>>>>>>AdminInfoController.gatherData>>>>>>>>>>" + simpleDateFormat.format(new Date()) + ">>>>>>>>>>>>amountSum:" + amountSumBg.toString());

        // 保底额度
        BigDecimal baodiBig = new BigDecimal(clockConfigModel.getBaodiAmount()).multiply(new BigDecimal(clockUserList.size()));
        // 待分配额度
        BigDecimal daiBig = amountSumBg.subtract(new BigDecimal(adminInfoModel.getForMeAmount())).subtract(baodiBig);

        // 循环所有已打卡用户列表
        // 对于今日已打卡人，打卡状态不变，分奖金（奖池-抽水-保底 按照份额分配 + 保底）生成第二日打卡资格，存入数据库，账户变动log记录
        // 今日打卡满21日，分奖金，押金进余额，是否还有押金剩余，有剩余生成第二日打卡资格
        for (UserAccountModel userAccountModel : clockUserList) {
            List<UserAccountLogModel> userAccountLogModelList = userAccountLogDao.findAllByOpenidAndTypeAndNoAndTypeFlagOrderByCreateDateDesc(userAccountModel.getOpenid(), "1", "0", "1");
            UserAccountLogModel userAccountLogModel = userAccountLogModelList.get(0);

            // 判断是否满21天
            int days = Days.daysBetween(new DateTime(new Date()), new DateTime(userAccountLogModel.getCreateDate())).getDays();

            // 剩余押金
            Double d = new BigDecimal(userAccountModel.getUseBalance0()).subtract(new BigDecimal(userAccountLogModel.getAmount())).doubleValue();

            // 更新用户账户信息
            userAccountModel.setBalance(new BigDecimal(userAccountLogModel.getAmount()).add(new BigDecimal(userAccountModel.getBalance())).toString());

            // 当前时间满21天 押金进余额 是否还有押金剩余，有剩余生成第二日打卡资格
            if (days >= clockConfigModel.getClockTime()) {
                // 修改该条log无效
                userAccountLogModel.setTypeFlag("0");

                if (d > 0) { // 如果押金还有剩余
                    // 生成第二天打卡资格
                    createClockUser(userAccountModel.getOpenid(), userAccountModel.getUseBalance0());
                } else { // 押金无剩余
                    // 修改账户信息
                    userAccountModel.setUseBalance0(null);
                    userAccountModel.setType0(null);
                    userAccountModel.setUseBalance0(null);
                }
                // 增加账户变动log，押金付款到余额
                UserAccountLogModel userAccountLogModel1 = new UserAccountLogModel();
                userAccountLogModel1.setAmount(userAccountLogModel.getAmount());
                userAccountLogModel1.setNo("0");
                userAccountLogModel1.setCreateDate(new Date());
                userAccountLogModel1.setOpenid(userAccountModel.getOpenid());
                userAccountLogModel1.setType("5");

                // log保存至数据库
                userAccountLogDao.save(userAccountLogModel);
                userAccountLogDao.save(userAccountLogModel1);

            } else { // 时间未满21天，正常处理数据
                createClockUser(userAccountModel.getOpenid(), userAccountModel.getUseBalance0());
            }

            // 分奖金
            // 按份额分配 个人押金 / 总押金 * 待分配额度
            BigDecimal personAmount = new BigDecimal(userAccountLogModel.getAmount()).divide(new BigDecimal(clockUserBalance0Sum), 2, BigDecimal.ROUND_HALF_UP).multiply(daiBig).setScale(2, BigDecimal.ROUND_HALF_UP);
            // 个人账户赋值
            userAccountModel.setBalance(new BigDecimal(userAccountModel.getBalance()).add(personAmount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            // 增加账户变动log 奖金发放
            UserAccountLogModel userAccountLogModel2 = new UserAccountLogModel();
            userAccountLogModel2.setAmount(personAmount.toString());
            userAccountLogModel2.setNo("0");
            userAccountLogModel2.setCreateDate(new Date());
            userAccountLogModel2.setOpenid(userAccountModel.getOpenid());
            userAccountLogModel2.setType("2");
            userAccountLogDao.save(userAccountLogModel2);
        }


        return json;
    }

    private void createClockUser(String openid, String useBalance) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        NeedClockUserModel needClockUserModel = needClockUserDao.getByOpenidAndNeedDate(openid, new DateTime().plusDays(1).toDate());
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
}
