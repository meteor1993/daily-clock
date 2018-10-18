package com.springboot.dailyclock.wxMpShare.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 微信服务号分享表
 * @author: weishiyao
 * @create 2018-08-11
 **/
@Entity
@Table(name = "wx_mp_share")
public class WxMpShareModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    private Date createDate;

    private String openid;

    private String unionId;

    private String preUnionId;

    // 红包额度
    private String redBagAmount;

    // 红包标记位
    private String redBagFlag;

    // 上级奖金
    private String preAmount;

    // 上级奖金标记位
    private String preAmountFlag;

    private String wxMpHeadImg;

    private String wxMpName;

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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getPreUnionId() {
        return preUnionId;
    }

    public void setPreUnionId(String preUnionId) {
        this.preUnionId = preUnionId;
    }

    public String getRedBagAmount() {
        return redBagAmount;
    }

    public void setRedBagAmount(String redBagAmount) {
        this.redBagAmount = redBagAmount;
    }

    public String getRedBagFlag() {
        return redBagFlag;
    }

    public void setRedBagFlag(String redBagFlag) {
        this.redBagFlag = redBagFlag;
    }

    public String getPreAmount() {
        return preAmount;
    }

    public void setPreAmount(String preAmount) {
        this.preAmount = preAmount;
    }

    public String getPreAmountFlag() {
        return preAmountFlag;
    }

    public void setPreAmountFlag(String preAmountFlag) {
        this.preAmountFlag = preAmountFlag;
    }

    public String getWxMpHeadImg() {
        return wxMpHeadImg;
    }

    public void setWxMpHeadImg(String wxMpHeadImg) {
        this.wxMpHeadImg = wxMpHeadImg;
    }

    public String getWxMpName() {
        return wxMpName;
    }

    public void setWxMpName(String wxMpName) {
        this.wxMpName = wxMpName;
    }
}
