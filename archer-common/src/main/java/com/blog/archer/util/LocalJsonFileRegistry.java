package com.blog.archer.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author yuanhang
 */
//@Component
public class LocalJsonFileRegistry {

    protected static final Logger log = LoggerFactory.getLogger("template");

    private final String id = "templateid";
    private final String path = "schema/*.json";

    /**
     * 加载模板schema，json文件的文件名是templateId
     *
     * @param path 模板schema所在json文件目录
     * @return
     */
    public void loadSchema(String path) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                if (resource.exists()) {
                    Map<String, Object> map = JSON.parseObject(resource.getInputStream(),
                            StandardCharsets.UTF_8,
                            Map.class,
                            Feature.AutoCloseSource,
                            Feature.AllowComment,
                            Feature.AllowSingleQuotes,
                            Feature.UseBigDecimal);
                }
            }
        } catch (IOException e) {
            log.error("加载模板schema异常", e);
        }
    }

//    @PostConstruct
    public void load() {
        loadSchema(path);
    }
}
