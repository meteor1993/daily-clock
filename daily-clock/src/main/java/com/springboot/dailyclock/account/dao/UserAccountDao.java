package com.springboot.dailyclock.account.dao;

import com.springboot.dailyclock.account.model.UserAccountModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by weisy on 2018/5/15
 */
public interface UserAccountDao extends PagingAndSortingRepository<UserAccountModel, Long>, JpaSpecificationExecutor<UserAccountModel> {
    List<UserAccountModel> findAllByType0(String no);
}
