package com.springboot.dailyclock.sms.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 短信信息表
 * @author: weishiyao
 * @create: 2018-05-12 22:12
 **/
@Entity
@Table(name = "sms_info")
public class SMSModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    /**
     * 创建者的sessionID
     */
    @Column(name = "create_sessionID")
    private String createSessionID;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 验证码
     */
    @Column(name = "verification_code")
    private String verificationCode;

    /**
     * 验证码
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 是否有效
     * 0无效/1有效
     */
    @Column(name = "is_valid")
    private String isValid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateSessionID() {
        return createSessionID;
    }

    public void setCreateSessionID(String createSessionID) {
        this.createSessionID = createSessionID;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
