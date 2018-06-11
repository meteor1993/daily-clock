package com.springboot.springcloudwechatclient.sign.model;

import java.util.Date;

/**
 * @program: daily-clock
 * @description: 基础配置表
 * @author: weishiyao
 * @create: 2018-05-12 21:40
 **/
public class ClockConfigModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 打卡开始时间
     */
    private String clockStartTime;

    /**
     * 打卡结束时间
     */
    private String clockEndTime;

    /**
     * 连续打卡时间 单位(天)
     */
    private int clockTime;

    /**
     * 抽取金额
     */
    private String forMeAmount;

    /**
     * 保底金额
     */
    private String baodiAmount;

    private Date createDate;

    private Date updateDate;

    /**
     * 当前盘口是否开放1.开放2.未开放
     */
    private String status;

    /**
     * 投资上限
     */
    private String balanceTopLine;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClockStartTime() {
        return clockStartTime;
    }

    public void setClockStartTime(String clockStartTime) {
        this.clockStartTime = clockStartTime;
    }

    public String getClockEndTime() {
        return clockEndTime;
    }

    public void setClockEndTime(String clockEndTime) {
        this.clockEndTime = clockEndTime;
    }

    public int getClockTime() {
        return clockTime;
    }

    public void setClockTime(int clockTime) {
        this.clockTime = clockTime;
    }

    public String getForMeAmount() {
        return forMeAmount;
    }

    public void setForMeAmount(String forMeAmount) {
        this.forMeAmount = forMeAmount;
    }

    public String getBaodiAmount() {
        return baodiAmount;
    }

    public void setBaodiAmount(String baodiAmount) {
        this.baodiAmount = baodiAmount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBalanceTopLine() {
        return balanceTopLine;
    }

    public void setBalanceTopLine(String balanceTopLine) {
        this.balanceTopLine = balanceTopLine;
    }
}