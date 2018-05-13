package com.springboot.dailyclock.sign.dao;

import com.springboot.dailyclock.sign.model.UserClockLogModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 22:00
 **/
public interface UserClockLogDao extends PagingAndSortingRepository<UserClockLogModel, Long>, JpaSpecificationExecutor<UserClockLogModel> {
}
