package com.springboot.dailyclock.sms.dao;

import com.springboot.dailyclock.sms.model.SMSModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-12 22:15
 **/
public interface SMSDao extends PagingAndSortingRepository<SMSModel, Long>, JpaSpecificationExecutor<SMSModel> {

    /**
     * 根据sessionid查询短信列表
     * @param sessionID
     * @return
     */
    List<SMSModel> findAllByCreateSessionIDOrderByCreateDateDesc(String sessionID);

    /**
     * 根据sessionid和是否有效查询短信列表
     * @param sessionID
     * @param isValid
     * @return
     */
    List<SMSModel> findAllByCreateSessionIDAndIsValidAndVerificationCodeOrderByCreateDateDesc(String sessionID, String isValid, String code);
}
