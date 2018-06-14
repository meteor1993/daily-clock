package com.springboot.dailyclock.sms.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.springboot.dailyclock.sms.dao.SMSDao;
import com.springboot.dailyclock.system.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @program: daily-clock
 * @description: 短信工具类
 * @author: weishiyao
 * @create: 2018-05-13 10:15
 **/
public class AliyunSMSUtils {

    private static final Logger logger = LoggerFactory.getLogger(AliyunSMSUtils.class);

    // 单例模式
    private AliyunSMSUtils() {}
    private static final AliyunSMSUtils aliyunSMSUtils = new AliyunSMSUtils();
    //静态工厂方法
    public static AliyunSMSUtils getInstance() {
        return aliyunSMSUtils;
    }

    @Autowired
    SMSDao smsDao;

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    private String accessKeyId = "LTAINranU2RSpGiW";
    private String accessKeySecret = "Ctmm4nRvURO9WpJ1IWlJoZloExnOMr";

    /**
     * 阿里云发送短信
     * @param mobile
     * @param code
     * @return
     */
    public boolean sendAliyunSMS(String mobile, String code) {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", this.accessKeyId, this.accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            logger.info(ExceptionUtil.getStackMsg(e));
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("开心打卡");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_136861370");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            logger.info(ExceptionUtil.getStackMsg(e));
            e.printStackTrace();
        }

        if ("OK".equals(sendSmsResponse.getCode())) {
            return true;
        }
        return false;
    }
}
