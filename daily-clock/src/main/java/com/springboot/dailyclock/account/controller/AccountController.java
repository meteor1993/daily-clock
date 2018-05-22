package com.springboot.dailyclock.account.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.ProductDao;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.model.ProductModel;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-17 20:04
 **/
@RestController
@RequestMapping(path = "/api")
public class AccountController {

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    ProductDao productDao;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    /**
     * 查询个人账户信息
     * @param openid
     * @return
     */
    @PostMapping(value = "/accountInfo")
    public CommonJson accountInfo(@RequestParam String openid) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>AccountController.accountInfo, openid:" + openid);
        CommonJson json = new CommonJson();
        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userAccountModel", userAccountModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultData(map);
        json.setResultMsg("成功");
        return json;
    }

    /**
     * 查询所有的产品
     * @return
     */
    @PostMapping(value = "/findProductList")
    public CommonJson findProductList() {
        CommonJson json = new CommonJson();
        List<ProductModel> productList = productDao.findAll();
        Map<String, Object> map = Maps.newHashMap();
        map.put("productList", productList);
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("成功");
        return json;
    }

    /**
     * 根据ProductNo查询Product
     * @param productNo
     * @return
     */
    @PostMapping(value = "/getProductByProductNo")
    public CommonJson getProductByProductNo(@RequestParam String productNo) {
        CommonJson json = new CommonJson();
        ProductModel productModel = productDao.getByProductNoIs(productNo);
        Map<String, Object> map = Maps.newHashMap();
        map.put("productModel", productModel);
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("成功");
        return json;
    }
}
