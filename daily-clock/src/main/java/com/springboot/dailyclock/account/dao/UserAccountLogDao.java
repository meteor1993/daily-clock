package com.springboot.dailyclock.account.dao;

import com.springboot.dailyclock.account.model.UserAccountLogModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserAccountLogDao extends PagingAndSortingRepository<UserAccountLogModel, Long>, JpaSpecificationExecutor<UserAccountLogModel> {
}
