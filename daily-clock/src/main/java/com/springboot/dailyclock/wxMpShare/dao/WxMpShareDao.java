package com.springboot.dailyclock.wxMpShare.dao;

import com.springboot.dailyclock.wxMpShare.model.WxMpShareModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface WxMpShareDao extends PagingAndSortingRepository<WxMpShareModel, Long>, JpaSpecificationExecutor<WxMpShareModel> {

    WxMpShareModel getAllByUnionId(String unionId);

    List<WxMpShareModel> findAllByPreUnionId(String unionId);

}
