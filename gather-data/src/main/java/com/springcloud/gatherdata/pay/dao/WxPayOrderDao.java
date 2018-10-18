package com.springcloud.gatherdata.pay.dao;

import com.springcloud.gatherdata.pay.model.WxPayOrderModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface WxPayOrderDao extends PagingAndSortingRepository<WxPayOrderModel, Long>, JpaSpecificationExecutor<WxPayOrderModel> {

    /**
     * 查询所有当日8点前充值记录
     * @param openid
     * @return
     */
    @Query("select u from WxPayOrderModel u where u.orderStatus = '1' and TIMESTAMPDIFF(hour, u.payTime, ?1) < 24 and TIMESTAMPDIFF(hour, u.payTime, ?1) > 0 and u.openId = ?2")
    List<WxPayOrderModel> findAllTodayPayOrder(String nowDate, String openid);

}
