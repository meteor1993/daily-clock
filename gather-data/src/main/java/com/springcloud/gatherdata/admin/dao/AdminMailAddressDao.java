package com.springcloud.gatherdata.admin.dao;


import com.springcloud.gatherdata.admin.model.AdminMailAddressModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 15:02
 **/
public interface AdminMailAddressDao extends PagingAndSortingRepository<AdminMailAddressModel, Long>, JpaSpecificationExecutor<AdminMailAddressModel> {

    List<AdminMailAddressModel> findAllByType(String type);
}
