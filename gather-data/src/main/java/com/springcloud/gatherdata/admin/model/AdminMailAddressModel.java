package com.springcloud.gatherdata.admin.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: daily-clock
 * @description: 通知邮件地址
 * @author: weishiyao
 * @create: 2018-05-20 14:59
 **/
@Entity
@Table(name = "admin_mail_address")
public class AdminMailAddressModel {

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name ="id",nullable=false,length=36)
    private String id;

    private Date createDate;

    private String mailAddress;

    /**
     * 业务模块 1.数据汇总通知 2.监控平台通知
     */
    private String type;

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

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
