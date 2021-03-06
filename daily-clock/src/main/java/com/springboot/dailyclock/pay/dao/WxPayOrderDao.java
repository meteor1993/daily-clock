package com.springboot.dailyclock.pay.dao;

import com.springboot.dailyclock.pay.model.WxPayOrderModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by weisy on 2018/5/15
 */
public interface WxPayOrderDao extends PagingAndSortingRepository<WxPayOrderModel, Long>, JpaSpecificationExecutor<WxPayOrderModel> {
    List<WxPayOrderModel> findAllByOpenIdAndOrderStatusOrderByOrderTimeDesc(String openid, String orderStatus);

    WxPayOrderModel getByIdIs(String id);

    WxPayOrderModel getByOrderNo(String orderNo);

    /**
     * 查询所有当日8点前充值记录
     * @param nowDate
     * @param openid
     * @return
     */
//    @Query("select u from WxPayOrderModel u where u.orderStatus = '1' and TO_DAYS(now()) - TO_DAYS(u.payTime) = 0 and hour(u.payTime) < 8 and u.openId = ?1")
    @Query("select u from WxPayOrderModel u where u.orderStatus = '1' and TIMESTAMPDIFF(hour, u.payTime, ?1) < 24 and TIMESTAMPDIFF(hour, u.payTime, ?1) > 0 and u.openId = ?2")
    List<WxPayOrderModel> findAllTodayPayOrder(String nowDate, String openid);
}
