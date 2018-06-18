package com.springboot.dailyclock.sign.dao;

import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-14 21:57
 **/
public interface NeedClockUserDao extends PagingAndSortingRepository<NeedClockUserModel, Long>, JpaSpecificationExecutor<NeedClockUserModel>{

    List<NeedClockUserModel> findAllByNeedDateBetweenAndNo(Date startDate, Date endDate, String no);

    List<NeedClockUserModel> findAll();

    /**
     * 根据时间查找需打卡人
     * @return
     */
    @Query("select n from NeedClockUserModel n where n.no = '0' and n.openid = ?1 and TO_DAYS(n.createDate) = TO_DAYS(?2)")
    NeedClockUserModel getByOpenidAndNeedDate(String openid, Date needDate);

    /**
     * 盘口0当日所有需打卡人
     * @param now
     * @return
     */
    @Query("select n.openid from NeedClockUserModel n where n.no = '0' and TO_DAYS(n.needDate) = TO_DAYS(?1)")
    List<String> findAllNeedClockUserModel(Date now);

    /**
     * 查询 某日期某盘口所有押金
     * @param no
     * @param date
     * @return
     */
    @Query("select sum (n.useBalance) from NeedClockUserModel n where n.no = ?1 and TO_DAYS(n.createDate) = TO_DAYS(?2)")
    String getbalanceSumByNo(String no, Date date);
}
