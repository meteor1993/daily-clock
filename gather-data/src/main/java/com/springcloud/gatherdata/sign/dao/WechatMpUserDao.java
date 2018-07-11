package com.springcloud.gatherdata.sign.dao;


import com.springcloud.gatherdata.sign.model.WechatMpUserModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-13 19:41
 **/
public interface WechatMpUserDao extends PagingAndSortingRepository<WechatMpUserModel, Long>, JpaSpecificationExecutor<WechatMpUserModel> {

    WechatMpUserModel getByWechatOpenIdIs(String openid);

    WechatMpUserModel getByMobileIs(String mobile);
}
