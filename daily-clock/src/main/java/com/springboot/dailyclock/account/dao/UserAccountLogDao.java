package com.springboot.dailyclock.account.dao;

import com.springboot.dailyclock.account.model.UserAccountLogModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserAccountLogDao extends PagingAndSortingRepository<UserAccountLogModel, Long>, JpaSpecificationExecutor<UserAccountLogModel> {

    List<UserAccountLogModel> findAllByOpenidAndTypeAndNoAndTypeFlagOrderByCreateDateAsc(String openid, String type, String no, String typeFlag);

    @Query("select round(sum (u.amount), 0) from UserAccountLogModel u where u.no = '0' and u.type = '2' and u.openid = ?1")
    String getAmountSum(String openid);

    UserAccountLogModel getByOrderNo(String orderNo);

    @Query("select u from UserAccountLogModel u where (u.type = '2' or u.type = '3' or u.type = '7') and u.openid = ?1 and MONTH(u.createDate) = MONTH(?2) AND YEAR(u.createDate) = YEAR(?2) order by u.createDate desc")
    List<UserAccountLogModel> findUserAccountLogPage(String openid, String date);

}
