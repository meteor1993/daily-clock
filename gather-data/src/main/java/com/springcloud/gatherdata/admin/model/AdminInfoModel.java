package com.springcloud.gatherdata.admin.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 每日后台总数据统计表
 * @author: weishiyao
 * @create: 2018-05-16 23:30
 **/
@Entity
@Table(name = "admin_info")
public class AdminInfoModel {

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    private Date createDate;

    private Date updateDate;

    /**
     * 盘口
     */
    private String no;

    /**
     * 打卡成功总人数
     */
    private String sumClockUser;

    /**
     * 打卡成功总金额
     */
    private String sumClockAmount;

    /**
     * 未打卡总人数
     */
    private String sumUnClockUser;

    /**
     * 未打卡总金额
     */
    private String sumUnClockAmount;

    /**
     * 千份奖金
     */
    private String qianfenAmount;

    /**
     * 平台补贴
     */
    private String subsidy;

    /**
     * 未打卡总金额
     */
    private String unClockAmountSum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSumClockUser() {
        return sumClockUser;
    }

    public void setSumClockUser(String sumClockUser) {
        this.sumClockUser = sumClockUser;
    }

    public String getSumClockAmount() {
        return sumClockAmount;
    }

    public void setSumClockAmount(String sumClockAmount) {
        this.sumClockAmount = sumClockAmount;
    }

    public String getSumUnClockUser() {
        return sumUnClockUser;
    }

    public void setSumUnClockUser(String sumUnClockUser) {
        this.sumUnClockUser = sumUnClockUser;
    }

    public String getSumUnClockAmount() {
        return sumUnClockAmount;
    }

    public void setSumUnClockAmount(String sumUnClockAmount) {
        this.sumUnClockAmount = sumUnClockAmount;
    }

    public String getQianfenAmount() {
        return qianfenAmount;
    }

    public void setQianfenAmount(String qianfenAmount) {
        this.qianfenAmount = qianfenAmount;
    }

    public String getSubsidy() {
        return subsidy;
    }

    public void setSubsidy(String subsidy) {
        this.subsidy = subsidy;
    }

    public String getUnClockAmountSum() {
        return unClockAmountSum;
    }

    public void setUnClockAmountSum(String unClockAmountSum) {
        this.unClockAmountSum = unClockAmountSum;
    }
}
