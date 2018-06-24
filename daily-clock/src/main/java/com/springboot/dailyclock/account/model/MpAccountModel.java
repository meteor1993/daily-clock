package com.springboot.dailyclock.account.model;

public class MpAccountModel {
    private String clockDate0;
    private String balanceSum0;
    private String continuousClockNum;
    private String wechatHeadImgUrl;

    public String getClockDate0() {
        return clockDate0;
    }

    public void setClockDate0(String clockDate0) {
        this.clockDate0 = clockDate0;
    }

    public String getBalanceSum0() {
        return balanceSum0;
    }

    public void setBalanceSum0(String balanceSum0) {
        this.balanceSum0 = balanceSum0;
    }

    public String getContinuousClockNum() {
        return continuousClockNum;
    }

    public void setContinuousClockNum(String continuousClockNum) {
        this.continuousClockNum = continuousClockNum;
    }

    public String getWechatHeadImgUrl() {
        return wechatHeadImgUrl;
    }

    public void setWechatHeadImgUrl(String wechatHeadImgUrl) {
        this.wechatHeadImgUrl = wechatHeadImgUrl;
    }
}
