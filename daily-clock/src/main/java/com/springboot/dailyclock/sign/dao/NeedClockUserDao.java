package com.springboot.dailyclock.sign.dao;

import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    List<NeedClockUserModel> findAllByCreateDateBetweenAndNo(Date startDate, Date endDate, String no);

    List<NeedClockUserModel> findAll();
}
