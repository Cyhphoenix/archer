package com.blog.archer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author yuanhang
 */
@Slf4j
@Component
public class MailUtil {

    private final JavaMailSender javaMailSender;

    private final MailProperties mailProperties;

    @Autowired
    public MailUtil(JavaMailSender javaMailSender, MailProperties mailProperties) {
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
    }

    /**
     * 发送文本格式的简单邮件邮件
     *
     * @param to
     * @param content
     * @param subject
     */
    public void simple(String subject, String content, String... to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.mailProperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        this.javaMailSender.send(message);
    }

    /**
     * 发送html内容邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    public void html(String subject, String content, String... to) {
        mailSend(subject, content, true, null, false, to);
    }

    /**
     * 发送带内联附件的邮件（附件内联在邮件内容中）
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  邮件内容
     * @param html     是否是html内容
     * @param resource 附件内容
     * @param inline   是否选择内联附件
     */
    public void attachment(String subject, String content, boolean html, Resource resource, boolean inline, String... to) {
        mailSend(subject, content, html, resource, inline, to);
    }


    /**
     * @param to       收件人
     * @param subject  主题
     * @param content  邮件内容
     * @param html     是否是html内容
     * @param resource 附件内容
     * @param inline   是否选择内联附件
     */
    private void mailSend(String subject, String content, boolean html, Resource resource, boolean inline, String... to) {
        try {
            MimeMessage message = this.javaMailSender.createMimeMessage();
            boolean multipart = resource == null ? false : true;
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, multipart);

            messageHelper.setFrom(this.mailProperties.getUsername());
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, html);
            if (null != resource) {
                if (true == inline) {
                    messageHelper.addInline("attach", resource);
                } else {
                    messageHelper.addAttachment("送一张萌猫的图片", resource);
                }
            }
            this.javaMailSender.send(message);
        } catch (MessagingException e) {
            log.info("邮件发送失败", e);
        }
    }


}
