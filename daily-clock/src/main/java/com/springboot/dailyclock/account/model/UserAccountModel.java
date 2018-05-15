package com.springboot.dailyclock.account.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 用户账户表
 * @author: weishiyao
 * @create: 2018-05-12 22:41
 **/
@Entity
@Table(name = "user_account")
public class UserAccountModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    private String openid;

    private Date createDate;

    private Date updateDate;

    /**
     * 账户余额
     */
    private String balance;

    /**
     * 盘口0使用中的金额
     */
    private String useBalance0;

    /**
     * 盘口1使用中的金额
     */
    private String useBalance1;

    /**
     * 盘口2中使用的金额
     */
    private String useBalance2;

    /**
     * 盘口3中使用的金额
     */
    private String useBalance3;

    /**
     * 盘口0的状态 0无1有
     */
    private String type0;

    /**
     * 盘口1的状态 0无1有
     */
    private String type1;

    /**
     * 盘口2的状态 0无1有
     */
    private String type2;

    /**
     * 盘口3的状态 0无1有
     */
    private String type3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUseBalance0() {
        return useBalance0;
    }

    public void setUseBalance0(String useBalance0) {
        this.useBalance0 = useBalance0;
    }

    public String getUseBalance1() {
        return useBalance1;
    }

    public void setUseBalance1(String useBalance1) {
        this.useBalance1 = useBalance1;
    }

    public String getUseBalance2() {
        return useBalance2;
    }

    public void setUseBalance2(String useBalance2) {
        this.useBalance2 = useBalance2;
    }

    public String getUseBalance3() {
        return useBalance3;
    }

    public void setUseBalance3(String useBalance3) {
        this.useBalance3 = useBalance3;
    }

    public String getType0() {
        return type0;
    }

    public void setType0(String type0) {
        this.type0 = type0;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getType3() {
        return type3;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }
}
