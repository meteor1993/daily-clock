package com.springboot.dailyclock.pay.dao;

import com.springboot.dailyclock.pay.model.WxPayOrderModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by weisy on 2018/5/15
 */
public interface WxPayOrderDao extends PagingAndSortingRepository<WxPayOrderModel, Long>, JpaSpecificationExecutor<WxPayOrderModel> {
    List<WxPayOrderModel> findAllByOpenIdAndOrderStatusOrderByOrderTimeDesc(String openid, String orderStatus);
}
