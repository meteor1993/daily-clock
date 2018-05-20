package com.springboot.springcloudwechatclient.sign.model;

import java.util.Date;

/**
 * @program: daily-clock
 * @description: 微信用户绑定表
 * @author: weishiyao
 * @create: 2018-05-12 21:40
 **/
public class WechatMpUserModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 创建时间
     */
    private Date createDate;

    private String createStringDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 微信openid
     */
    private String wechatOpenId;

    /**
     * 微信unionId
     */
    private String wechatUnionId;

    /**
     * 微信用户头像
     */
    private String wechatHeadImgUrl;

    /**
     * 微信用户语言
     */
    private String wechatLanguage;

    /**
     * 微信用户城市
     */
    private String wechatCity;

    /**
     * 微信用户省份
     */
    private String wechatProvince;

    /**
     * 微信用户国家
     */
    private String wechatCountry;

    /**
     * 微信用户昵称
     */
    private String wechatNickName;

    /**
     * 微信用户性别
     */
    private String wechatSex;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 年龄
     */
    private String age;

    /**
     * 职业
     */
    private String professional;

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

    public String getCreateStringDate() {
        return createStringDate;
    }

    public void setCreateStringDate(String createStringDate) {
        this.createStringDate = createStringDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getWechatOpenId() {
        return wechatOpenId;
    }

    public void setWechatOpenId(String wechatOpenId) {
        this.wechatOpenId = wechatOpenId;
    }

    public String getWechatUnionId() {
        return wechatUnionId;
    }

    public void setWechatUnionId(String wechatUnionId) {
        this.wechatUnionId = wechatUnionId;
    }

    public String getWechatHeadImgUrl() {
        return wechatHeadImgUrl;
    }

    public void setWechatHeadImgUrl(String wechatHeadImgUrl) {
        this.wechatHeadImgUrl = wechatHeadImgUrl;
    }

    public String getWechatLanguage() {
        return wechatLanguage;
    }

    public void setWechatLanguage(String wechatLanguage) {
        this.wechatLanguage = wechatLanguage;
    }

    public String getWechatCity() {
        return wechatCity;
    }

    public void setWechatCity(String wechatCity) {
        this.wechatCity = wechatCity;
    }

    public String getWechatProvince() {
        return wechatProvince;
    }

    public void setWechatProvince(String wechatProvince) {
        this.wechatProvince = wechatProvince;
    }

    public String getWechatCountry() {
        return wechatCountry;
    }

    public void setWechatCountry(String wechatCountry) {
        this.wechatCountry = wechatCountry;
    }

    public String getWechatNickName() {
        return wechatNickName;
    }

    public void setWechatNickName(String wechatNickName) {
        this.wechatNickName = wechatNickName;
    }

    public String getWechatSex() {
        return wechatSex;
    }

    public void setWechatSex(String wechatSex) {
        this.wechatSex = wechatSex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }
}
