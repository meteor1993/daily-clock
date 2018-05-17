package com.springboot.dailyclock.sign.dao;

import com.springboot.dailyclock.sign.model.UserClockLogModel;
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

    /**
     * 查询当日打卡列表，按时间正序
     * @param now
     * @return
     */
    @Query("select u.openId from UserClockLogModel u where u.no=?1 and u.type = '1' and TO_DAYS(u.createDate) = TO_DAYS(?2) order by u.createDate asc")
    List<String> findEarlyClockUser(String no, Date now);
}
