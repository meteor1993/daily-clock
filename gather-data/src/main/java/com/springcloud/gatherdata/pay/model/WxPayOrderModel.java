package com.springcloud.gatherdata.pay.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wechat_pay_order", schema = "")
public class WxPayOrderModel {

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

	private String payType; // 支付方式

	private Date updateDate;

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=36)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name ="order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name ="order_time")
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	@Column(name ="order_money")
	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	@Column(name ="order_comment")
	public String getOrderComment() {
		return orderComment;
	}

	public void setOrderComment(String orderComment) {
		this.orderComment = orderComment;
	}
	
	@Column(name ="order_status")
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Column(name ="openId")
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Column(name ="pay_time")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Column(name ="tx_order_no")
	public String getTxOrderNo() {
		return txOrderNo;
	}

	public void setTxOrderNo(String txOrderNo) {
		this.txOrderNo = txOrderNo;
	}

	@Column(name ="pay_success_time")
	public String getPaySuccessTime() {
		return paySuccessTime;
	}

	public void setPaySuccessTime(String paySuccessTime) {
		this.paySuccessTime = paySuccessTime;
	}

	@Column(name ="transaction_id")
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name ="pay_times")
	public Integer getPayTimes() {
		return payTimes;
	}

	public void setPayTimes(Integer payTimes) {
		this.payTimes = payTimes;
	}
	
	@Column(name ="product_no")
	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	@Column(name ="pay_type")
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
