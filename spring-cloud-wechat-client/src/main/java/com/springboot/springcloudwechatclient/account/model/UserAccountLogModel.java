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
     * 账户交易业务类型，1.充值2.奖金发放3.提现4.余额到押金（零钱支付）5.押金到余额（打卡周期结束）6.打卡失败押金清零7.上级账户发放奖金8.账户红包入账
     */
    private String type;

    private String openid;

    /**
     * 交易金额
     */
    private String amount;

    /**
     * 来源盘口
     */
    private String no;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 充值是否有效 1.有效0.无效
     */
    private String typeFlag;

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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(String typeFlag) {
        this.typeFlag = typeFlag;
    }
}
