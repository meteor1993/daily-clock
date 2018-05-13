package com.springboot.dailyclock.sign.dao;

import com.springboot.dailyclock.sign.model.SignConfigModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 20:41
 **/
public interface SignConfigDao extends PagingAndSortingRepository<SignConfigModel, Long>, JpaSpecificationExecutor<SignConfigModel> {
}
