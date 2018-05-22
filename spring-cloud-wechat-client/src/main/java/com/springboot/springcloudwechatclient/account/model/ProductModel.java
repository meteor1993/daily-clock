package com.springboot.springcloudwechatclient.account.model;

/**
 * Created by weisy on 2018/5/22
 */
public class ProductModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String productNo;

    private String productName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
