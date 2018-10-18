//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.binarywang.wxpay.bean.result;

import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import me.chanjar.weixin.common.util.ToStringUtils;
import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class BaseWxPayResult implements Serializable {
    @XStreamAlias("return_code")
    protected String returnCode;
    @XStreamAlias("return_msg")
    protected String returnMsg;
    @XStreamAlias("result_code")
    private String resultCode;
    @XStreamAlias("err_code")
    private String errCode;
    @XStreamAlias("err_code_des")
    private String errCodeDes;
    @XStreamAlias("appid")
    private String appid;
    @XStreamAlias("mch_id")
    private String mchId;
    @XStreamAlias("sub_appid")
    private String subAppId;
    @XStreamAlias("sub_mch_id")
    private String subMchId;
    @XStreamAlias("nonce_str")
    private String nonceStr;
    @XStreamAlias("sign")
    private String sign;
    private String xmlString;
    private Document xmlDoc;

    public static String feeToYuan(Integer fee) {
        return (new BigDecimal(Double.valueOf((double)fee) / 100.0D)).setScale(2, 4).toPlainString();
    }

    public static <T extends BaseWxPayResult> T fromXML(String xmlString, Class<T> clz) {
        XStream xstream = XStreamInitializer.getInstance();
        XStream.setupDefaultSecurity(xstream);
        // xstream 如果转换类型有安全问题，在这里添加
        xstream.allowTypes(new Class[]{EntPayResult.class, BaseWxPayResult.class, WxPayUnifiedOrderResult.class, WxPayOrderQueryResult.class, WxPayOrderNotifyResult.class});
        xstream.processAnnotations(clz);
        T result = (T) xstream.fromXML(xmlString);
        result.setXmlString(xmlString);
        return result;
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    public String toString() {
        return ToStringUtils.toSimpleString(this);
    }

    public Map<String, String> toMap() {
        if (StringUtils.isBlank(this.xmlString)) {
            throw new RuntimeException("xml数据有问题，请核实！");
        } else {
            Map<String, String> result = Maps.newHashMap();
            Document doc = this.getXmlDoc();

            try {
                NodeList list = (NodeList)XPathFactory.newInstance().newXPath().compile("/xml/*").evaluate(doc, XPathConstants.NODESET);
                int len = list.getLength();

                for(int i = 0; i < len; ++i) {
                    result.put(list.item(i).getNodeName(), list.item(i).getTextContent());
                }

                return result;
            } catch (XPathExpressionException var6) {
                throw new RuntimeException("非法的xml文本内容：" + this.xmlString);
            }
        }
    }

    private Document getXmlDoc() {
        if (this.xmlDoc != null) {
            return this.xmlDoc;
        } else {
            try {
                this.xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(this.xmlString.getBytes("UTF-8")));
                return this.xmlDoc;
            } catch (IOException | ParserConfigurationException | SAXException var2) {
                throw new RuntimeException("非法的xml文本内容：" + this.xmlString);
            }
        }
    }

    String getXmlValue(String... path) {
        Document doc = this.getXmlDoc();
        String expression = String.format("/%s//text()", Joiner.on("/").join(path));

        try {
            return (String)XPathFactory.newInstance().newXPath().compile(expression).evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException var5) {
            throw new RuntimeException("未找到相应路径的文本：" + expression);
        }
    }

    Integer getXmlValueAsInt(String... path) {
        String result = this.getXmlValue(path);
        return StringUtils.isBlank(result) ? null : Integer.valueOf(result);
    }

    public void checkResult(WxPayService wxPayService, String signType, boolean checkSuccess) throws WxPayException {
        Map<String, String> map = this.toMap();
        if (this.getSign() != null && !SignUtils.checkSign(map, signType, wxPayService.getConfig().getMchKey())) {
            this.getLogger().debug("校验结果签名失败，参数：{}", map);
            throw new WxPayException("参数格式校验错误！");
        } else {
            if (checkSuccess) {
                List<String> successStrings = Lists.newArrayList(new String[]{"SUCCESS", ""});
                if (!successStrings.contains(StringUtils.trimToEmpty(this.getReturnCode()).toUpperCase()) || !successStrings.contains(StringUtils.trimToEmpty(this.getResultCode()).toUpperCase())) {
                    StringBuilder errorMsg = new StringBuilder();
                    if (this.getReturnCode() != null) {
                        errorMsg.append("返回代码：").append(this.getReturnCode());
                    }

                    if (this.getReturnMsg() != null) {
                        errorMsg.append("，返回信息：").append(this.getReturnMsg());
                    }

                    if (this.getResultCode() != null) {
                        errorMsg.append("，结果代码：").append(this.getResultCode());
                    }

                    if (this.getErrCode() != null) {
                        errorMsg.append("，错误代码：").append(this.getErrCode());
                    }

                    if (this.getErrCodeDes() != null) {
                        errorMsg.append("，错误详情：").append(this.getErrCodeDes());
                    }

                    this.getLogger().error("\n结果业务代码异常，返回结果：{},\n{}", map, errorMsg.toString());
                    throw WxPayException.from(this);
                }
            }

        }
    }

    public BaseWxPayResult() {
    }

    public String getReturnCode() {
        return this.returnCode;
    }

    public String getReturnMsg() {
        return this.returnMsg;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrCodeDes() {
        return this.errCodeDes;
    }

    public String getAppid() {
        return this.appid;
    }

    public String getMchId() {
        return this.mchId;
    }

    public String getSubAppId() {
        return this.subAppId;
    }

    public String getSubMchId() {
        return this.subMchId;
    }

    public String getNonceStr() {
        return this.nonceStr;
    }

    public String getSign() {
        return this.sign;
    }

    public String getXmlString() {
        return this.xmlString;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setXmlString(String xmlString) {
        this.xmlString = xmlString;
    }

    public void setXmlDoc(Document xmlDoc) {
        this.xmlDoc = xmlDoc;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseWxPayResult)) {
            return false;
        } else {
            BaseWxPayResult other = (BaseWxPayResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label167: {
                    Object this$returnCode = this.getReturnCode();
                    Object other$returnCode = other.getReturnCode();
                    if (this$returnCode == null) {
                        if (other$returnCode == null) {
                            break label167;
                        }
                    } else if (this$returnCode.equals(other$returnCode)) {
                        break label167;
                    }

                    return false;
                }

                Object this$returnMsg = this.getReturnMsg();
                Object other$returnMsg = other.getReturnMsg();
                if (this$returnMsg == null) {
                    if (other$returnMsg != null) {
                        return false;
                    }
                } else if (!this$returnMsg.equals(other$returnMsg)) {
                    return false;
                }

                label153: {
                    Object this$resultCode = this.getResultCode();
                    Object other$resultCode = other.getResultCode();
                    if (this$resultCode == null) {
                        if (other$resultCode == null) {
                            break label153;
                        }
                    } else if (this$resultCode.equals(other$resultCode)) {
                        break label153;
                    }

                    return false;
                }

                Object this$errCode = this.getErrCode();
                Object other$errCode = other.getErrCode();
                if (this$errCode == null) {
                    if (other$errCode != null) {
                        return false;
                    }
                } else if (!this$errCode.equals(other$errCode)) {
                    return false;
                }

                label139: {
                    Object this$errCodeDes = this.getErrCodeDes();
                    Object other$errCodeDes = other.getErrCodeDes();
                    if (this$errCodeDes == null) {
                        if (other$errCodeDes == null) {
                            break label139;
                        }
                    } else if (this$errCodeDes.equals(other$errCodeDes)) {
                        break label139;
                    }

                    return false;
                }

                Object this$appid = this.getAppid();
                Object other$appid = other.getAppid();
                if (this$appid == null) {
                    if (other$appid != null) {
                        return false;
                    }
                } else if (!this$appid.equals(other$appid)) {
                    return false;
                }

                label125: {
                    Object this$mchId = this.getMchId();
                    Object other$mchId = other.getMchId();
                    if (this$mchId == null) {
                        if (other$mchId == null) {
                            break label125;
                        }
                    } else if (this$mchId.equals(other$mchId)) {
                        break label125;
                    }

                    return false;
                }

                label118: {
                    Object this$subAppId = this.getSubAppId();
                    Object other$subAppId = other.getSubAppId();
                    if (this$subAppId == null) {
                        if (other$subAppId == null) {
                            break label118;
                        }
                    } else if (this$subAppId.equals(other$subAppId)) {
                        break label118;
                    }

                    return false;
                }

                Object this$subMchId = this.getSubMchId();
                Object other$subMchId = other.getSubMchId();
                if (this$subMchId == null) {
                    if (other$subMchId != null) {
                        return false;
                    }
                } else if (!this$subMchId.equals(other$subMchId)) {
                    return false;
                }

                label104: {
                    Object this$nonceStr = this.getNonceStr();
                    Object other$nonceStr = other.getNonceStr();
                    if (this$nonceStr == null) {
                        if (other$nonceStr == null) {
                            break label104;
                        }
                    } else if (this$nonceStr.equals(other$nonceStr)) {
                        break label104;
                    }

                    return false;
                }

                label97: {
                    Object this$sign = this.getSign();
                    Object other$sign = other.getSign();
                    if (this$sign == null) {
                        if (other$sign == null) {
                            break label97;
                        }
                    } else if (this$sign.equals(other$sign)) {
                        break label97;
                    }

                    return false;
                }

                Object this$xmlString = this.getXmlString();
                Object other$xmlString = other.getXmlString();
                if (this$xmlString == null) {
                    if (other$xmlString != null) {
                        return false;
                    }
                } else if (!this$xmlString.equals(other$xmlString)) {
                    return false;
                }

                Object this$xmlDoc = this.getXmlDoc();
                Object other$xmlDoc = other.getXmlDoc();
                if (this$xmlDoc == null) {
                    if (other$xmlDoc != null) {
                        return false;
                    }
                } else if (!this$xmlDoc.equals(other$xmlDoc)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseWxPayResult;
    }

    public int hashCode() {
        boolean PRIME = true;
        int results = 1;
        Object $returnCode = this.getReturnCode();
        int result = results * 59 + ($returnCode == null ? 43 : $returnCode.hashCode());
        Object $returnMsg = this.getReturnMsg();
        result = result * 59 + ($returnMsg == null ? 43 : $returnMsg.hashCode());
        Object $resultCode = this.getResultCode();
        result = result * 59 + ($resultCode == null ? 43 : $resultCode.hashCode());
        Object $errCode = this.getErrCode();
        result = result * 59 + ($errCode == null ? 43 : $errCode.hashCode());
        Object $errCodeDes = this.getErrCodeDes();
        result = result * 59 + ($errCodeDes == null ? 43 : $errCodeDes.hashCode());
        Object $appid = this.getAppid();
        result = result * 59 + ($appid == null ? 43 : $appid.hashCode());
        Object $mchId = this.getMchId();
        result = result * 59 + ($mchId == null ? 43 : $mchId.hashCode());
        Object $subAppId = this.getSubAppId();
        result = result * 59 + ($subAppId == null ? 43 : $subAppId.hashCode());
        Object $subMchId = this.getSubMchId();
        result = result * 59 + ($subMchId == null ? 43 : $subMchId.hashCode());
        Object $nonceStr = this.getNonceStr();
        result = result * 59 + ($nonceStr == null ? 43 : $nonceStr.hashCode());
        Object $sign = this.getSign();
        result = result * 59 + ($sign == null ? 43 : $sign.hashCode());
        Object $xmlString = this.getXmlString();
        result = result * 59 + ($xmlString == null ? 43 : $xmlString.hashCode());
        Object $xmlDoc = this.getXmlDoc();
        result = result * 59 + ($xmlDoc == null ? 43 : $xmlDoc.hashCode());
        return result;
    }
}
