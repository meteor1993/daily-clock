package com.springboot.dailyclock.admin.dao;

import com.springboot.dailyclock.admin.model.AdminInfoModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 14:47
 **/
public interface AdminInfoDao extends PagingAndSortingRepository<AdminInfoModel, Long>, JpaSpecificationExecutor<AdminInfoModel> {
}
