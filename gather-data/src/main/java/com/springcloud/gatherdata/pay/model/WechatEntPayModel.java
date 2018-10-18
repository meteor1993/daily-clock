package com.springcloud.gatherdata.pay.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 企业付款记录表
 * @author: weishiyao
 * @create: 2018-05-22 20:47
 **/
@Entity
@Table(name = "wechat_ent_pay", schema = "")
public class WechatEntPayModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    //*****************************腾讯请求参数***************************************
    @Column(name = "device_info")
    private String device_info;

    @Column(name = "nonce_str")
    private String nonce_str;

    @Column(name = "partner_trade_no")
    private String partner_trade_no;

    @Column(name = "openid")
    private String openid;

    @Column(name = "check_name")
    private String check_name;

    @Column(name = "amount")
    private int amount;

    @Column(name = "ent_desc")
    private String desc;

    @Column(name = "spbill_create_ip")
    private String spbill_create_ip;
    //*************************腾讯请求参数结束************************************************

    @Column(name = "status")
    private String status;

    @Column(name = "send_date")
    private String sendDate;

    @Column(name = "send_msg")
    private String sendMsg;

    @Column(name = "false_reason")
    private String falseReason;

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

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getPartner_trade_no() {
        return partner_trade_no;
    }

    public void setPartner_trade_no(String partner_trade_no) {
        this.partner_trade_no = partner_trade_no;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCheck_name() {
        return check_name;
    }

    public void setCheck_name(String check_name) {
        this.check_name = check_name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }

    public String getFalseReason() {
        return falseReason;
    }

    public void setFalseReason(String falseReason) {
        this.falseReason = falseReason;
    }
}
