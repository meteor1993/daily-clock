package com.springboot.springcloudwechatclient.admin.model;

import java.util.Date;

/**
 * @program: daily-clock
 * @description: 每日后台总数据统计表
 * @author: weishiyao
 * @create: 2018-05-16 23:30
 **/
public class AdminInfoModel {

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
     * 最终抽水金额
     */
    private String forMeAmount;

    /**
     * 千份奖金
     */
    private String qianfenAmount;

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

    public String getForMeAmount() {
        return forMeAmount;
    }

    public void setForMeAmount(String forMeAmount) {
        this.forMeAmount = forMeAmount;
    }

    public String getQianfenAmount() {
        return qianfenAmount;
    }

    public void setQianfenAmount(String qianfenAmount) {
        this.qianfenAmount = qianfenAmount;
    }
}
