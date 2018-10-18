package com.springboot.dailyclock.account.service.impl;

import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.account.service.AccountService;
import com.springboot.dailyclock.pay.dao.WxPayOrderDao;
import com.springboot.dailyclock.pay.model.WxPayOrderModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AccountServiceI implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceI.class);

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    WxPayOrderDao wxPayOrderDao;


    @Override
    public List<UserAccountModel> findAllUnClockUser(Date nowDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 查询所有当日未打卡账户
        List<UserAccountModel> userAccountModelList = userAccountDao.findAllUnClockUser(nowDate);
        this.logger.info("当前查询所有当日未打卡账户记录数为：" + userAccountModelList.size());
        // 查询所有当日充值的账户
        List<UserAccountModel> userAccountModels = userAccountDao.findAllTodayUser();
        this.logger.info("当前查询充值的账户记录数为：" + userAccountModels.size());

        // 当日8点前如果有充值记录，则放入未打卡账户中
        for (UserAccountModel model : userAccountModels) {
            List<WxPayOrderModel> wxPayOrderModelList = wxPayOrderDao.findAllTodayPayOrder(simpleDateFormat.format(new Date()) + " 08:00:00", model.getOpenid());
            this.logger.info("当前查询账户充值记录数为：" + wxPayOrderModelList.size() + "参数时间为:" + simpleDateFormat.format(new Date()) + " 08:00:00" + ", openid:" + model.getOpenid());
            if (wxPayOrderModelList.size() > 0) {
                userAccountModelList.add(model);
            }
        }

        return userAccountModelList;
    }

    @Override
    public String getUnClockUserBalance0Sum(Date now) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 查询所有当日未打卡账户
        List<UserAccountModel> userAccountModelList = userAccountDao.findAllUnClockUser(now);
        this.logger.info("当前查询所有当日未打卡账户记录数为：" + userAccountModelList.size());
        // 查询所有当日充值的账户
        List<UserAccountModel> userAccountModels = userAccountDao.findAllTodayUser();
        this.logger.info("当前查询充值的账户记录数为：" + userAccountModels.size());

        // 当日8点前如果有充值记录，则放入未打卡账户中
        for (UserAccountModel model : userAccountModels) {
            List<WxPayOrderModel> wxPayOrderModelList = wxPayOrderDao.findAllTodayPayOrder(simpleDateFormat.format(new Date()) + " 08:00:00", model.getOpenid());
            this.logger.info("当前查询账户充值记录数为：" + wxPayOrderModelList.size() + "参数时间为:" + simpleDateFormat.format(new Date()) + " 08:00:00" + ", openid:" + model.getOpenid());
            if (wxPayOrderModelList.size() > 0) {
                userAccountModelList.add(model);
            }
        }

        BigDecimal bigDecimal = new BigDecimal("0");

        // 统计所有未打卡总金额
        for (UserAccountModel model : userAccountModelList) {
            bigDecimal = bigDecimal.add(new BigDecimal(model.getUseBalance0())).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    @Override
    public String getUnClockUserCount0(Date now) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 查询所有当日未打卡账户
        List<UserAccountModel> userAccountModelList = userAccountDao.findAllUnClockUser(now);
        this.logger.info("当前查询所有当日未打卡账户记录数为：" + userAccountModelList.size());
        // 查询所有当日充值的账户
        List<UserAccountModel> userAccountModels = userAccountDao.findAllTodayUser();
        this.logger.info("当前查询充值的账户记录数为：" + userAccountModels.size());

        // 当日8点前如果有充值记录，则放入未打卡账户中
        for (UserAccountModel model : userAccountModels) {
            List<WxPayOrderModel> wxPayOrderModelList = wxPayOrderDao.findAllTodayPayOrder(simpleDateFormat.format(new Date()) + " 08:00:00", model.getOpenid());
            this.logger.info("当前查询账户充值记录数为：" + wxPayOrderModelList.size() + "参数时间为:" + simpleDateFormat.format(new Date()) + " 08:00:00" + ", openid:" + model.getOpenid());
            if (wxPayOrderModelList.size() > 0) {
                userAccountModelList.add(model);
            }
        }

        return String.valueOf(userAccountModelList.size());
    }
}
