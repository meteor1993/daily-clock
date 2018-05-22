package com.springboot.dailyclock.pay.dao;

import com.springboot.dailyclock.pay.model.WechatEntPayModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-22 20:52
 **/
public interface WechatEntPayDao extends PagingAndSortingRepository<WechatEntPayModel, Long>, JpaSpecificationExecutor<WechatEntPayModel> {
}