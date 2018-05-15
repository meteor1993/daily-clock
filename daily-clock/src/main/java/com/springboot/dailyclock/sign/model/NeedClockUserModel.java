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
     * 盘口0，1，2，3
     */
    private String no;

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "NeedClockUserModel{" +
                "id='" + id + '\'' +
                ", openid='" + openid + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
