package com.springboot.dailyclock.account.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.account.dao.ProductDao;
import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.account.dao.UserAccountLogDao;
import com.springboot.dailyclock.account.model.MpAccountModel;
import com.springboot.dailyclock.account.model.ProductModel;
import com.springboot.dailyclock.account.model.UserAccountLogModel;
import com.springboot.dailyclock.account.model.UserAccountModel;
import com.springboot.dailyclock.account.service.AccountService;
import com.springboot.dailyclock.admin.dao.AdminInfoDao;
import com.springboot.dailyclock.admin.model.AdminInfoModel;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.dao.WechatMpUserDao;
import com.springboot.dailyclock.sign.model.UserClockLogModel;
import com.springboot.dailyclock.sign.model.WechatMpUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Autowired
    UserClockLogDao userClockLogDao;

    @Autowired
    UserAccountLogDao userAccountLogDao;

    @Autowired
    NeedClockUserDao needClockUserDao;

    @Autowired
    WechatMpUserDao wechatMpUserDao;

    @Autowired
    AdminInfoDao adminInfoDao;

    @Autowired
    AccountService accountService;

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

    @PostMapping(value = "/saveAccountModel")
    public CommonJson saveAccountModel(@RequestBody UserAccountModel userAccountModel) {
        CommonJson json = new CommonJson();
        UserAccountModel userAccountModel1 = userAccountDao.save(userAccountModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userAccountModel", userAccountModel1);
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("成功");
        return json;
    }

    /**
     * 保存进出帐日志
     * @param userAccountLogModel
     * @return
     */
    @PostMapping(value = "/saveAccountModelLog")
    public CommonJson saveAccountModelLog(@RequestBody UserAccountLogModel userAccountLogModel) {
        CommonJson json = new CommonJson();
        UserAccountLogModel userAccountLogModel1 = userAccountLogDao.save(userAccountLogModel);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userAccountLogModel", userAccountLogModel1);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("succee");
        json.setResultData(map);
        return json;
    }

    /**
     * 根据订单号查询进出帐日志
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getAccountModelLogByOrderNo")
    public CommonJson getAccountModelLogByOrderNo(@RequestParam String orderNo) {
        CommonJson json = new CommonJson();
        UserAccountLogModel userAccountLogModel = userAccountLogDao.getByOrderNo(orderNo);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userAccountLogModel", userAccountLogModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("succee");
        json.setResultData(map);
        return json;
    }

    /**
     * 账户中心数据查询
     * @param openid
     * @param no
     * @return
     */
    @PostMapping(value = "/accountCenter")
    public CommonJson accountCenter(@RequestParam String openid, @RequestParam String no) {
        CommonJson json = new CommonJson();
//        List<UserAccountLogModel> userAccountLogModelList = userAccountLogDao.findAllByOpenidAndTypeAndNoOrderByCreateDateDesc(openid, type, no);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        AdminInfoModel adminInfoModel = adminInfoDao.getAdminInfoModelByCreateDate(simpleDateFormat.format(new Date()));

//        String amountSum = userAccountLogDao.getAmountSum(openid);

        List<UserClockLogModel> userClockLogModelList = userClockLogDao.findAllByOpenIdOrderByCreateDateDesc(openid);

        UserAccountModel userAccountModel = userAccountDao.getByOpenidIs(openid);

        WechatMpUserModel wechatMpUserModel = wechatMpUserDao.getByWechatOpenIdIs(openid);

        // 当日总打卡金额
        String clockBalanceSum = userClockLogDao.clockBalanceSum(no, new Date());

        // 当日未打卡金额
        String unClockBalanceSum = accountService.getUnClockUserBalance0Sum(new Date());

        // 如果admininfo中未打卡总金额不为null，则显示admininfo中的数据
        if (adminInfoModel != null && adminInfoModel.getUnClockAmountSum() != null) {
            unClockBalanceSum = adminInfoModel.getUnClockAmountSum();
        }

        Map<String, Object> map = Maps.newHashMap();
//        map.put("amountSum", amountSum);
        map.put("userClockLogSize", String.valueOf(userClockLogModelList.size()));
        map.put("userAccountModel", userAccountModel);
        map.put("wechatMpUserModel", wechatMpUserModel);
        map.put("clockBalanceSum", clockBalanceSum);
        map.put("unClockBalanceSum", unClockBalanceSum);

        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        return json;
    }

    /**
     * 查询当前用户进出账记录
     * @param openid
     * @param date
     * @return
     */
    @PostMapping(value = "/findUserAccountLogList")
    public CommonJson findUserAccountLogList(@RequestParam String openid, @RequestParam String date) {
        logger.info(">>>>>>>>>>AccountController.findUserAccountLogList>>>>>>>>>>>>openid:" + openid + ">>>>>>>date:" + date);
        List<UserAccountLogModel> userAccountLogModelList = userAccountLogDao.findUserAccountLogPage(openid, date);
        Map<String, Object> map = Maps.newHashMap();
        map.put("list", userAccountLogModelList);

        CommonJson json = new CommonJson();
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        return json;
    }

    /**
     * 分页查询首页list
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/findMpAccountList")
    public CommonJson findMpAccountList(@RequestParam int page, @RequestParam int size) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Page<Map<String,Object>> listPage = userAccountDao.findMpAccountList(new Date(), new PageRequest(page, size));
        List<Map<String,Object>> mapList = listPage.getContent();
        for (Map<String, Object> maps : mapList) {
            maps.put("0", simpleDateFormat.format(maps.get("0")));
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("list", mapList);

        CommonJson json = new CommonJson();
        json.setResultData(map);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        return json;
    }
}
