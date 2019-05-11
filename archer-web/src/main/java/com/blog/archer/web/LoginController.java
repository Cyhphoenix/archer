package com.blog.archer.web;

import com.blog.archer.response.ResponseObj;
import com.blog.archer.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuanhang
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    private static Map<String, String> holder = new ConcurrentHashMap<>(8);
    private final Logger logger = LoggerFactory.getLogger("login");

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/signUp")
    public Object signUp(@RequestParam(value = "username") String username,
                         @RequestParam(value = "password") String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            return ResponseObj.success("用户名或密码格式不正确");
        }
        username = username.trim();
        password = password.trim();

        if (holder.containsKey(username.toLowerCase())) {
            return ResponseObj.success("用户名已存在");

        } else {
            holder.put(username.toLowerCase(), EncryptUtil.encode(EncryptUtil.ALGO_MD5, password));
        }

        logger.info("Sign up success : username:[" + username + "], password:[" + password + "]");
        return ResponseObj.success("注册成功，欢迎 " + username);
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/signIn")
    public Object signIn(@RequestParam(value = "username") String username,
                         @RequestParam(value = "password") String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            return ResponseObj.success("用户名或密码错误");
        }
        username = username.trim();
        password = password.trim();

        if (EncryptUtil.encode(EncryptUtil.ALGO_MD5, password).equals(holder.get(username.toLowerCase()))) {
            return ResponseObj.success("登录成功，欢迎 " + username);

        } else {
            return ResponseObj.success("用户名或密码错误");
        }

    }



    @PreDestroy
    public void init() {
        logger.info("====== Local Memory refresh, all clear ======");
    }

}
