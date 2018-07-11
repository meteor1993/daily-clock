package com.springcloud.gatherdata.system.mail.service.impl;

import com.springcloud.gatherdata.system.mail.dao.SendMailLogDao;
import com.springcloud.gatherdata.system.mail.model.SendMailLogModel;
import com.springcloud.gatherdata.system.mail.service.MailService;
import com.springcloud.gatherdata.system.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @program: daily-clock
 * @description:
 * @author: weishiyao
 * @create: 2018-05-20 14:43
 **/
@Service
public class MailServiceImpl implements MailService {


    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    SendMailLogDao sendMailLogDao;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        // ---------------------创建邮件发送日志-----------------------
        SendMailLogModel sendMailLogModel = new SendMailLogModel();
        sendMailLogModel.setEmailFrom(emailFrom);
        sendMailLogModel.setEmailTo(to);
        sendMailLogModel.setCreateDate(new Date());
        sendMailLogModel.setSubject(subject);
        sendMailLogModel.setContent(content);
        try {
            // ------------------发送邮件-------------------
            javaMailSender.send(message);
            sendMailLogModel.setStatus(true);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>sendSimpleMail发送成功>>>>>>>>>>>>>>>>>>>>>>>>>");
        } catch (Exception e) {
            sendMailLogModel.setStatus(false);
            sendMailLogModel.setFailReason(ExceptionUtil.getStackMsg(e));
            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>sendSimpleMail产生异常>>>>>>>>>>>>>>>>>>>>>", e);
        } finally {
            sendMailLogDao.save(sendMailLogModel);
        }
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        SendMailLogModel sendMailLogModel = new SendMailLogModel();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // ---------------------创建邮件发送日志-----------------------
            sendMailLogModel.setEmailFrom(emailFrom);
            sendMailLogModel.setEmailTo(to);
            sendMailLogModel.setCreateDate(new Date());
            sendMailLogModel.setSubject(subject);
            sendMailLogModel.setContent(content);
            // ------------------发送邮件-------------------
            javaMailSender.send(message);
            sendMailLogModel.setStatus(true);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>sendHtmlMail发送成功>>>>>>>>>>>>>>>>>>>>>>>>>");
        } catch (MessagingException e) {
            sendMailLogModel.setStatus(false);
            sendMailLogModel.setFailReason(ExceptionUtil.getStackMsg(e));
            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>sendHtmlMail产生异常>>>>>>>>>>>>>>>>>>>>>", e);
        } finally {
            sendMailLogDao.save(sendMailLogModel);
        }
    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, List<String> filePaths) {
        MimeMessage message = javaMailSender.createMimeMessage();

        SendMailLogModel sendMailLogModel = new SendMailLogModel();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // ---------------------创建邮件发送日志-----------------------
            sendMailLogModel.setEmailFrom(emailFrom);
            sendMailLogModel.setEmailTo(to);
            sendMailLogModel.setCreateDate(new Date());
            sendMailLogModel.setSubject(subject);
            sendMailLogModel.setContent(content);

            String string = "";

            for (String filePath : filePaths){
                FileSystemResource file = new FileSystemResource(new File(filePath));
                String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
                helper.addAttachment(fileName, file);
                filePath = filePath + ";";
                string += filePath;
            }
            sendMailLogModel.setAttachments(string);

            javaMailSender.send(message);
            sendMailLogModel.setStatus(true);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>sendAttachmentsMail发送成功>>>>>>>>>>>>>>>>>>>>>>>>>");
        } catch (MessagingException e) {
            sendMailLogModel.setStatus(false);
            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>sendAttachmentsMail产生异常>>>>>>>>>>>>>>>>>>>>>", e);
        } finally {
            sendMailLogDao.save(sendMailLogModel);
        }

    }
}
