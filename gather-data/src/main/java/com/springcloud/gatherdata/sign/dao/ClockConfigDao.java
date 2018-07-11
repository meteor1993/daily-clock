package com.springcloud.gatherdata.sign.dao;


import com.springcloud.gatherdata.sign.model.ClockConfigModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 20:41
 **/
public interface ClockConfigDao extends PagingAndSortingRepository<ClockConfigModel, Long>, JpaSpecificationExecutor<ClockConfigModel> {

    ClockConfigModel getByIdIs(String no);

}
