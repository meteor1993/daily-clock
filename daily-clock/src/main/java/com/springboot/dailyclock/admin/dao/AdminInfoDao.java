package com.springboot.dailyclock.admin.dao;

import com.springboot.dailyclock.admin.model.AdminInfoModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 14:47
 **/
public interface AdminInfoDao extends PagingAndSortingRepository<AdminInfoModel, Long>, JpaSpecificationExecutor<AdminInfoModel> {

    @Query("select a from AdminInfoModel a where TO_DAYS(a.createDate) = TO_DAYS(?1)")
    AdminInfoModel getAdminInfoModelByCreateDate(String date);

}
