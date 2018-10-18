package com.springboot.dailyclock.account.service;

import com.springboot.dailyclock.account.model.UserAccountModel;

import java.util.Date;
import java.util.List;

public interface AccountService {
    /**
     * 查询所有当日未打卡账户
     * @param nowDate
     * @return
     */
    List<UserAccountModel> findAllUnClockUser(Date nowDate);

    /**
     * 盘口0当日未打卡总金额
     * @param now
     * @return
     */
    String getUnClockUserBalance0Sum(Date now);

    /**
     * 盘口0当日未打卡总人数
     * @param now
     * @return
     */
    String getUnClockUserCount0(Date now);
}
