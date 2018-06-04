package com.springboot.springcloudwechatclient.pay.model;

import java.util.Date;

public class WxPayOrderModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;//主键ID

	private String orderNo;//订单编号

	private Date orderTime;//订单创建时间

	private Double orderMoney;//订单金额

	private String orderComment;//订单描述

	private String orderStatus;//支付状态 0:创建 1：订单完成

	private String openId;//支付openId

	private Date payTime;//发起支付时间

	private String txOrderNo;//腾讯订单ID

	private String paySuccessTime;//支付完成时间

	private String transactionId;//微信支付订单号

	private Integer payTimes;//第几次下单

	private String productNo; //商品编号

	private Date updateDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	public String getOrderComment() {
		return orderComment;
	}

	public void setOrderComment(String orderComment) {
		this.orderComment = orderComment;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getTxOrderNo() {
		return txOrderNo;
	}

	public void setTxOrderNo(String txOrderNo) {
		this.txOrderNo = txOrderNo;
	}

	public String getPaySuccessTime() {
		return paySuccessTime;
	}

	public void setPaySuccessTime(String paySuccessTime) {
		this.paySuccessTime = paySuccessTime;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getPayTimes() {
		return payTimes;
	}

	public void setPayTimes(Integer payTimes) {
		this.payTimes = payTimes;
	}
	
	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
