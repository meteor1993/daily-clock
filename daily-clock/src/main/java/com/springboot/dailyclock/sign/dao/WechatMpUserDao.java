package com.springboot.dailyclock.sign.dao;

import com.springboot.dailyclock.sign.model.WechatMpUserModel;
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

    WechatMpUserModel getByWechatUnionId(String unionId);
}
