package com.springboot.springcloudwechatclient.pay.model;

import java.util.Date;

/**
 * @program: daily-clock
 * @description: 企业付款记录表
 * @author: weishiyao
 * @create: 2018-05-22 20:47
 **/
public class WechatEntPayModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Date createDate;

    private Date updateDate;

    //*****************************腾讯请求参数***************************************
    private String device_info;

    private String nonce_str;

    private String partner_trade_no;

    private String openid;

    private String check_name;

    private int amount;

    private String desc;

    private String spbill_create_ip;
    //*************************腾讯请求参数结束************************************************

    // 0失败1成功2提交审批
    private String status;

    private Date sendDate;

    private String sendMsg;

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

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
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
