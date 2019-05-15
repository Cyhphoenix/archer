package com.blog.archer.web;

import com.blog.archer.response.ResponseObj;
import com.blog.archer.util.EncryptUtil;
import com.blog.archer.util.MailUtil;
import com.blog.archer.vo.SignInVo;
import com.blog.archer.vo.SignUpVo;
import freemarker.template.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuanhang
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    private static Map<String, String> holder = new ConcurrentHashMap<>(8);
    private final Logger logger = LoggerFactory.getLogger("login");

    private final MailUtil mailUtil;
    private final Configuration freemarkerConfiguration;

    @Autowired
    public LoginController(MailUtil mailUtil, Configuration freemarkerConfiguration) {
        this.mailUtil = mailUtil;
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    /**
     * 注册
     *
     * @return
     */
    @PostMapping("/signUp")
    public Object signUp(SignUpVo vo) {
        if (null == vo) {
            return ResponseObj.success("参数错误");
        }
        final String username = vo.getUsername().trim();
        final String password = vo.getPassword().trim();
        final String mail = vo.getMail().trim();
        if (StringUtils.isAnyBlank(username, password, mail)) {
            return ResponseObj.success("用户名或密码格式不正确");
        }

        if (holder.containsKey(username.toLowerCase())) {
            return ResponseObj.success("用户名已存在");

        } else {
            holder.put(username.toLowerCase(), EncryptUtil.encode(EncryptUtil.ALGO_MD5, password));
        }

        CompletableFuture.runAsync(() -> signUpMail(username, mail));

        logger.info("Sign up success : username:[" + username + "], password:[" + password + "]");
        return ResponseObj.success("注册成功，欢迎 " + username);
    }

    /**
     * 登录
     *
     * @return
     */
    @PostMapping("/signIn")
    public Object signIn(SignInVo vo) {
        if (null == vo) {
            return ResponseObj.success("参数错误");
        }

        final String username = vo.getUsername().trim();
        final String password = vo.getPassword().trim();
        if (StringUtils.isAnyBlank(username, password)) {
            return ResponseObj.success("用户名或密码错误");
        }

        if (EncryptUtil.encode(EncryptUtil.ALGO_MD5, password).equals(holder.get(username.toLowerCase()))) {
            return ResponseObj.success("登录成功，欢迎 " + username);

        } else {
            return ResponseObj.success("用户名或密码错误");
        }

    }

    private void signUpMail(String username, String to) {
        Map<String, Object> model = new HashMap<>(1);
        model.put("username", username);
        try {
            // DEFAULT_TEMPLATE_LOADER_PATH = "classpath:/templates/"
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(this.freemarkerConfiguration.getTemplate("signup_mail.ftl"), model);
            mailUtil.attachment("博客账号注册成功通知", content, true, new ClassPathResource("attachment/cat.jpeg"), true, to);
            logger.info("注册邮件发送成功");
        } catch (Exception e) {
            logger.info("邮件发送异常", e);
        }
    }


    @PreDestroy
    public void refresh() {
        logger.info("====== Local Memory refresh, all clear ======");
    }

}
