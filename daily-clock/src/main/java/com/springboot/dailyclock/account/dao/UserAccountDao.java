package com.springboot.dailyclock.account.dao;

import com.springboot.dailyclock.account.model.MpAccountModel;
import com.springboot.dailyclock.account.model.UserAccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by weisy on 2018/5/15
 */
public interface UserAccountDao extends PagingAndSortingRepository<UserAccountModel, Long>, JpaSpecificationExecutor<UserAccountModel> {

    List<UserAccountModel> findAllByType0(String type);

    UserAccountModel getByOpenidIs(String openid);

    Page<UserAccountModel> findAllByType0OrderByOrderDate0Desc(String type0, Pageable pageable);

    /**
     * 查询盘口0所有当日打卡账户
     * @param nowDate
     * @return
     */
    @Query("select u from UserAccountModel u where u.type0 = '1' and TO_DAYS(u.clockDate0) = TO_DAYS(?1) and length(u.openid) > 5")
    List<UserAccountModel> findAllClockUser(Date nowDate);

    /**
     * 查询所有当日未打卡账户
     * @param nowDate
     * @return
     */
    @Query("select u from UserAccountModel u where u.type0 = '1' and TO_DAYS(?1) - TO_DAYS(u.clockDate0) >= 1")
    List<UserAccountModel> findAllUnClockUser(Date nowDate);

    /**
     * 查询所有当日充值的账户
     * @return
     */
    @Query("select u from UserAccountModel u where u.type0 = '1' and  u.clockDate0 is null")
    List<UserAccountModel> findAllTodayUser();

    /**
     * 盘口0所有账户总押金
     * @return
     */
    @Query("select round(sum (u.useBalance0), 0) from UserAccountModel u where u.type0 = '1' and length(u.openid) > 5")
    String getUserBalance0Sum();

    /**
     * 盘口0当日已打卡总金额
     * @param now
     * @return
     */
    @Query("select round(sum (u.useBalance0), 2) from UserAccountModel u where u.type0 = '1' and TO_DAYS(u.clockDate0) = TO_DAYS(?1) and length(u.openid) > 5")
    String getClockUserBalance0Sum(Date now);

    /**
     * 盘口0当日未打卡总金额
     * @param now
     * @return
     */
//    @Query("select round(sum (u.useBalance0), 2) from UserAccountModel u where u.type0 = '1' and u.useBalance0 <> '' and (TO_DAYS(?1) - TO_DAYS(u.clockDate0) >= 1 or u.clockDate0 is null)")
//    String getUnClockUserBalance0Sum(Date now);

    /**
     * 盘口0总账户数
     * @return
     */
    @Query("select count (1) from UserAccountModel u where u.type0 = '1' and length(u.openid) > 5")
    String getUserCount0();

    /**
     * 盘口0当日打卡总人数
     * @param now
     * @return
     */
    @Query("select count (1) from UserAccountModel u where u.type0 = '1' and TO_DAYS(u.clockDate0) = TO_DAYS(?1) and length(u.openid) > 5")
    String getClockUserCount0(Date now);

    /**
     * 盘口0当日未打卡总人数
     * @param now
     * @return
     */
//    @Query("select count (1) from UserAccountModel u where u.type0 = '1' and u.useBalance0 <> '' and (TO_DAYS(?1) - TO_DAYS(u.clockDate0) >= 1 or u.clockDate0 is null)")
//    String getUnClockUserCount0(Date now);

    @Query("select u from UserAccountModel u where u.type0 = '1' order by u.continuousClockNum desc")
    Page<UserAccountModel> getMaxContinuousClockUser(Pageable pageable);

    /**
     * 奖金倒序查询用户信息
     * @param pageable
     * @return
     */
    @Query("select new map(u.clockDate0, u.balanceSum0, u.continuousClockNum, w.wechatHeadImgUrl, w.wechatNickName) from UserAccountModel u, WechatMpUserModel w where u.openid = w.wechatOpenId and TO_DAYS(u.clockDate0) = TO_DAYS(?1) order by u.clockDate0")
    Page<Map<String,Object>> findMpAccountList(Date today, Pageable pageable);
}
