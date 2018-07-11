package com.springcloud.gatherdata.account.dao;


import com.springcloud.gatherdata.account.model.ProductModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by weisy on 2018/5/22
 */
public interface ProductDao extends PagingAndSortingRepository<ProductModel, Long>, JpaSpecificationExecutor<ProductModel> {
    List<ProductModel> findAll();

    ProductModel getByProductNoIs(String productNo);
}
