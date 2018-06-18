package com.springboot.springcloudwechatclient.account.model;

import java.util.Date;

/**
 * @program: daily-clock
 * @description: 用户账户表
 * @author: weishiyao
 * @create: 2018-05-12 22:41
 **/
public class UserAccountModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String openid;

    private Date createDate;

    private Date updateDate;

    /**
     * 账户余额
     */
    private String balance;

    /**
     * 盘口0使用中的金额
     */
    private String useBalance0;

    /**
     * 盘口1使用中的金额
     */
    private String useBalance1;

    /**
     * 盘口2中使用的金额
     */
    private String useBalance2;

    /**
     * 盘口3中使用的金额
     */
    private String useBalance3;

    /**
     * 盘口0的状态 0无1有
     */
    private String type0;

    /**
     * 盘口1的状态 0无1有
     */
    private String type1;

    /**
     * 盘口2的状态 0无1有
     */
    private String type2;

    /**
     * 盘口3的状态 0无1有
     */
    private String type3;

    /**
     * 盘口0最后缴费时间
     */
    private Date orderDate0;

    /**
     * 盘口1最后缴费时间
     */
    private Date orderDate1;

    /**
     * 盘口2最后缴费时间
     */
    private Date orderDate2;

    /**
     * 盘口3最后缴费时间
     */
    private Date orderDate3;

    /**
     * 盘口0上次打卡时间
     */
    private Date clockDate0;

    /**
     * 盘口1上次打卡时间
     */
    private Date clockDate1;

    /**
     * 盘口2上次打卡时间
     */
    private Date clockDate2;

    /**
     * 盘口3上次打卡时间
     */
    private Date clockDate3;

    /**
     * 盘口0 当日新增奖金
     */
    private String todayBalance0;

    /**
     * 盘口1 当日新增奖金
     */
    private String todayBalance1;

    /**
     * 盘口2 当日新增奖金
     */
    private String todayBalance2;

    /**
     * 盘口3 当日新增奖金
     */
    private String todayBalance3;

    /**
     * 连续打卡次数
     */
    private String continuousClockNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUseBalance0() {
        return useBalance0;
    }

    public void setUseBalance0(String useBalance0) {
        this.useBalance0 = useBalance0;
    }

    public String getUseBalance1() {
        return useBalance1;
    }

    public void setUseBalance1(String useBalance1) {
        this.useBalance1 = useBalance1;
    }

    public String getUseBalance2() {
        return useBalance2;
    }

    public void setUseBalance2(String useBalance2) {
        this.useBalance2 = useBalance2;
    }

    public String getUseBalance3() {
        return useBalance3;
    }

    public void setUseBalance3(String useBalance3) {
        this.useBalance3 = useBalance3;
    }

    public String getType0() {
        return type0;
    }

    public void setType0(String type0) {
        this.type0 = type0;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getType3() {
        return type3;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }

    public Date getOrderDate0() {
        return orderDate0;
    }

    public void setOrderDate0(Date orderDate0) {
        this.orderDate0 = orderDate0;
    }

    public Date getOrderDate1() {
        return orderDate1;
    }

    public void setOrderDate1(Date orderDate1) {
        this.orderDate1 = orderDate1;
    }

    public Date getOrderDate2() {
        return orderDate2;
    }

    public void setOrderDate2(Date orderDate2) {
        this.orderDate2 = orderDate2;
    }

    public Date getOrderDate3() {
        return orderDate3;
    }

    public void setOrderDate3(Date orderDate3) {
        this.orderDate3 = orderDate3;
    }

    public Date getClockDate0() {
        return clockDate0;
    }

    public void setClockDate0(Date clockDate0) {
        this.clockDate0 = clockDate0;
    }

    public Date getClockDate1() {
        return clockDate1;
    }

    public void setClockDate1(Date clockDate1) {
        this.clockDate1 = clockDate1;
    }

    public Date getClockDate2() {
        return clockDate2;
    }

    public void setClockDate2(Date clockDate2) {
        this.clockDate2 = clockDate2;
    }

    public Date getClockDate3() {
        return clockDate3;
    }

    public void setClockDate3(Date clockDate3) {
        this.clockDate3 = clockDate3;
    }

    public String getTodayBalance0() {
        return todayBalance0;
    }

    public void setTodayBalance0(String todayBalance0) {
        this.todayBalance0 = todayBalance0;
    }

    public String getTodayBalance1() {
        return todayBalance1;
    }

    public void setTodayBalance1(String todayBalance1) {
        this.todayBalance1 = todayBalance1;
    }

    public String getTodayBalance2() {
        return todayBalance2;
    }

    public void setTodayBalance2(String todayBalance2) {
        this.todayBalance2 = todayBalance2;
    }

    public String getTodayBalance3() {
        return todayBalance3;
    }

    public void setTodayBalance3(String todayBalance3) {
        this.todayBalance3 = todayBalance3;
    }

    public String getContinuousClockNum() {
        return continuousClockNum;
    }

    public void setContinuousClockNum(String continuousClockNum) {
        this.continuousClockNum = continuousClockNum;
    }
}
