package com.springcloud.gatherdata.sign.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 基础配置表
 * @author: weishiyao
 * @create: 2018-05-12 21:40
 **/
@Entity
@Table(name = "clock_config")
public class ClockConfigModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
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

    /**
     * 提现上限
     */
    private String getMoneyTopLine;

    /**
     * 平台补贴
     */
    private String subsidy;

    /**
     * 平台补贴标记位
     */
    private String subsidyFlag;

    /**
     * 奖励金上限
     */
    private String rewardBalanceLines;

    /**
     * 推荐奖金
     */
    private String bonus;

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

    public String getGetMoneyTopLine() {
        return getMoneyTopLine;
    }

    public void setGetMoneyTopLine(String getMoneyTopLine) {
        this.getMoneyTopLine = getMoneyTopLine;
    }

    public String getSubsidy() {
        return subsidy;
    }

    public void setSubsidy(String subsidy) {
        this.subsidy = subsidy;
    }

    public String getSubsidyFlag() {
        return subsidyFlag;
    }

    public void setSubsidyFlag(String subsidyFlag) {
        this.subsidyFlag = subsidyFlag;
    }

    public String getRewardBalanceLines() {
        return rewardBalanceLines;
    }

    public void setRewardBalanceLines(String rewardBalanceLines) {
        this.rewardBalanceLines = rewardBalanceLines;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
}
