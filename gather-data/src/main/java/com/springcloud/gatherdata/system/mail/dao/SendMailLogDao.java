package com.springcloud.gatherdata.system.mail.dao;


import com.springcloud.gatherdata.system.mail.model.SendMailLogModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 14:44
 **/
public interface SendMailLogDao extends PagingAndSortingRepository<SendMailLogModel, Long>, JpaSpecificationExecutor<SendMailLogModel> {
}
