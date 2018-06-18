package com.springboot.dailyclock.sign.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 需打卡表
 * @author: weishiyao
 * @create: 2018-05-14 20:15
 **/
@Entity
@Table(name = "need_clock_user")
public class NeedClockUserModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    private String openid;

    private Date createDate;

    /**
     * 需打卡时间
     */
    private Date needDate;

    /**
     * 盘口0，1，2，3
     */
    private String no;

    private String useBalance;

    /**
     * 订单号
     */
    private String orderNo;

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

    public Date getNeedDate() {
        return needDate;
    }

    public void setNeedDate(Date needDate) {
        this.needDate = needDate;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getUseBalance() {
        return useBalance;
    }

    public void setUseBalance(String useBalance) {
        this.useBalance = useBalance;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "NeedClockUserModel{" +
                "id='" + id + '\'' +
                ", openid='" + openid + '\'' +
                ", createDate=" + createDate +
                ", no='" + no + '\'' +
                ", useBalance='" + useBalance + '\'' +
                '}';
    }
}
