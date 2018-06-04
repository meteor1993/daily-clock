package com.springboot.dailyclock.account.dao;

import com.springboot.dailyclock.account.model.UserAccountModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by weisy on 2018/5/15
 */
public interface UserAccountDao extends PagingAndSortingRepository<UserAccountModel, Long>, JpaSpecificationExecutor<UserAccountModel> {

    List<UserAccountModel> findAllByType0(String type);

    UserAccountModel getByOpenidIs(String openid);

    /**
     * 盘口0所有账户总押金
     * @return
     */
    @Query("select round(sum (u.useBalance0), 0) from UserAccountModel u where u.type0 = '1'")
    String getUserBalance0Sum();

    /**
     * 盘口0当日打卡总金额
     * @param now
     * @return
     */
    @Query("select sum (u.useBalance0) from UserAccountModel u where u.type0 = '1' and TO_DAYS(u.clockDate0) = TO_DAYS(?1)")
    String getClockUserBalance0Sum(Date now);

    /**
     * 盘口0当日未打卡总金额
     * @param now
     * @return
     */
    @Query("select sum (u.useBalance0) from UserAccountModel u where u.type0 = '1' and u.useBalance0 <> '' and TO_DAYS(?1) - TO_DAYS(u.clockDate0) <= 1")
    String getUnClockUserBalance0Sum(Date now);

    /**
     * 盘口0总账户数
     * @return
     */
    @Query("select count (1) from UserAccountModel u where u.type0 = '1'")
    String getUserCount0();

    /**
     * 盘口0当日打卡总人数
     * @param now
     * @return
     */
    @Query("select count (1) from UserAccountModel u where u.type0 = '1' and TO_DAYS(u.clockDate0) = TO_DAYS(?1)")
    String getClockUserCount0(Date now);

    /**
     * 盘口0当日未打卡总人数
     * @param now
     * @return
     */
    @Query("select count (1) from UserAccountModel u where u.type0 = '1' and u.useBalance0 <> '' and TO_DAYS(?1) - TO_DAYS(u.clockDate0) >= 1")
    String getUnClockUserCount0(Date now);
}
