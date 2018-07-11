package com.springcloud.gatherdata.sign.dao;


import com.springcloud.gatherdata.sign.model.UserClockLogModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 22:00
 **/
public interface UserClockLogDao extends PagingAndSortingRepository<UserClockLogModel, Long>, JpaSpecificationExecutor<UserClockLogModel> {

    List<UserClockLogModel> findAllByOpenIdOrderByCreateDateDesc(String openid);

    @Query("select u from UserClockLogModel u where u.openId=?1 and u.type = '1' and TO_DAYS(u.createDate) = TO_DAYS(?2) order by u.createDate asc")
    UserClockLogModel getByOpenIdAndCreateDate(String openid, Date nowDate);

    /**
     * 查询当日打卡列表，按时间正序
     * @param now
     * @return
     */
    @Query("select u.openId from UserClockLogModel u where u.no=?1 and u.type = '1' and TO_DAYS(u.createDate) = TO_DAYS(?2) order by u.createDate asc")
    List<String> findEarlyClockUser(String no, Date now);

    /**
     * 查询当日打卡列表，按时间倒序
     * @param no
     * @param now
     * @return
     */
    @Query("select u.openId from UserClockLogModel u where u.no=?1 and u.type = '1' and TO_DAYS(u.createDate) = TO_DAYS(?2) order by u.createDate desc")
    List<String> findLaterClockUser(String no, Date now);

    /**
     * 根据盘口，日期查询当日打卡总押金
     * @param no
     * @param date
     * @return
     */
    @Query("select round(sum (u.useBalance), 2) from UserClockLogModel u where u.no=?1 and TO_DAYS(u.createDate) = TO_DAYS(?2) ")
    String clockBalanceSum(String no, Date date);
}
