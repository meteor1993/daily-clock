package com.springboot.dailyclock.admin.dao;

import com.springboot.dailyclock.admin.model.AdminConfigUserModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AdminConfigUserDao extends PagingAndSortingRepository<AdminConfigUserModel, Long>, JpaSpecificationExecutor<AdminConfigUserModel> {
    @Query("select a from AdminConfigUserModel a")
    List<AdminConfigUserModel> findAllUsers();
}
