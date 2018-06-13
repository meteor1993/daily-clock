package com.springboot.springcloudwechatclient.account.model;

import java.util.Date;

/**
 * @program: daily-clock
 * @description: 用户账户记录表
 * @author: weishiyao
 * @create: 2018-05-12 22:49
 **/
public class UserAccountLogModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Date createDate;

    private Date updateDate;

    /**
     * 账户交易业务类型，充值、提现
     */
    private String type;

    /**
     * 交易金额
     */
    private String amount;

    /**
     * 来源盘口
     */
    private String no;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
