package com.springcloud.gatherdata.sign.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 用户打卡记录表
 * @author: weishiyao
 * @create: 2018-05-12 21:40
 **/
@Entity
@Table(name = "user_clock_log")
public class UserClockLogModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    private String openId;

    private Date createDate;

    /**
     * 打卡类型 0.未打卡1.已打卡2.补打卡
     */
    private String type;

    /**
     * 盘口
     */
    private String no;

    /**
     * 打卡押金
     */
    private String useBalance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
