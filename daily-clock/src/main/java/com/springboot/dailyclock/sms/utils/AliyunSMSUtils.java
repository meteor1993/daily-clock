package com.springboot.dailyclock.sms.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.springboot.dailyclock.sms.dao.SMSDao;
import com.springboot.dailyclock.sms.model.SMSModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.system.utils.ContextHolderUtils;
import com.springboot.dailyclock.system.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 获取随机数
     * @return
     */
    private String getRandomCode() {
        Random rad = new Random();

        String result = rad.nextInt(1000000) + "";

        if(result.length() != 6){
            return getRandomCode();
        }
        return result;
    }

    /**
     * 发送短信验证码
     * @param mobile
     * @return
     */
    public boolean sendSMS(String mobile) {
        String code = getRandomCode();
        logger.info("---------------sendSMS--------------mobile:" + mobile + ",code" + code);
        boolean flag = sendAliyunSMS(mobile, code);
        if (flag) {
            SMSModel smsModel = new SMSModel();
            smsModel.setCreateDate(new Date());
            smsModel.setMobile(mobile);
            smsModel.setCreateSessionID(ContextHolderUtils.getSession().getId());
            smsModel.setIsValid(Constant.SUCCESS_CODE);
            smsModel.setVerificationCode(code);
            smsDao.save(smsModel);
            return true;
        }
        return false;
    }

    /**
     * 校验短信验证码
     * @param code
     * @param minutes 有效时间
     * @return
     */
    public CommonJson checkVerificationCode(String code, int minutes) {
        CommonJson json = new CommonJson();
        List<SMSModel> smsModelList = smsDao.findAllByCreateSessionIDAndIsValidOrderByCreateDateDesc(ContextHolderUtils.getSession().getId(), code);
        SMSModel smsModel = smsModelList.get(0);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String createDate = simpleFormat.format(smsModel.getCreateDate());
        String nowDate = simpleFormat.format(new Date());
        try {
            long create = simpleFormat.parse(createDate).getTime();
            long now = simpleFormat.parse(nowDate).getTime();
            int times = (int) ((now - create)/(1000 * 60));
            if (smsModelList.size() > 0) { // 查询到相关数据
                if (Constant.FAIL_CODE.equals(smsModel.getIsValid())) { // 判断当前验证码是否有效
                    if (times < minutes) { // 判断时间是否小于有效时间
                        // 将验证码变为无效
                        smsModel.setIsValid(Constant.FAIL_CODE);
                        smsDao.save(smsModel);
                        json.setResultCode(Constant.JSON_SUCCESS_CODE);
                        json.setResultMsg("验证码输入正确");
                    } else {
                        json.setResultCode(Constant.JSON_ERROR_CODE);
                        json.setResultMsg("当前验证码已过期，请重新获取");
                    }
                } else {
                    json.setResultCode(Constant.JSON_ERROR_CODE);
                    json.setResultMsg("您到验证码已失效，请重新获取");
                }
            } else {
                json.setResultCode(Constant.JSON_ERROR_CODE);
                json.setResultMsg("请输入正确到验证码");
            }
        } catch (ParseException e) {
            logger.info(ExceptionUtil.getStackMsg(e));
        }
        return json;
    }

    /**
     * 阿里云发送短信
     * @param mobile
     * @param code
     * @return
     */
    private boolean sendAliyunSMS(String mobile, String code) {
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
        request.setSignName("云通信");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_1000000");
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