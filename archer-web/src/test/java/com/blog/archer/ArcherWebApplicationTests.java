package com.blog.archer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArcherWebApplicationTests {

    @Test
    public void contextLoads() {
    }


    @Autowired
    MailProperties mailProperties;

    @Test
    public  void mailAutowired(){

        System.out.println(mailProperties.getHost());
        System.out.println(mailProperties.getUsername());
        System.out.println(mailProperties.getPassword());

    }

}
