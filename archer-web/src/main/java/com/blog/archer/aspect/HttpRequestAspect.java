package com.blog.archer.aspect;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuanhang
 */
@Aspect
@Component
public class HttpRequestAspect {

    private static final Logger logger = LoggerFactory.getLogger("request");
    private static final String THREAD_NAME_SUFFIX_FORMAT = "#%d#";
    private static final Pattern THREAD_NAME_SUFFIX_PATTERN = Pattern.compile("#\\d+#$");
    private static final String SEPARATOR_CHAR = ",";
    private static AtomicLong atomicLong = new AtomicLong();
    private final String CONTENT_TYPE = "multipart/form-data";
    private int BUFFER_SIZE = 1024 * 4;

    @Pointcut("execution(public * com.blog.archer.web.*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before() throws Throwable {
        long id = atomicLong.incrementAndGet();
        markThreadName(id);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String remoteAddr = request.getRemoteAddr();
        String uri = request.getRequestURI();
        Map<String, String[]> params = new HashMap<>(16);
        params.putAll(request.getParameterMap());
        if (CONTENT_TYPE.equals(request.getContentType())) {
            for (Part part : request.getParts()) {
                params.put(part.getName(), part.getContentType() == null
                        ? new String[]{read(part.getInputStream(), Charset.defaultCharset())}
                        : new String[]{"<size> : " + part.getSize()});
            }
        }

        logger.info(genRequestLog(remoteAddr, uri, params));
    }

    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void afterReturn(Object ret) {
        logger.info(genResponseLog(ret));
    }

    private void markThreadName(Long id) {
        Thread thread = Thread.currentThread();
        String name = thread.getName();
        Matcher matcher = THREAD_NAME_SUFFIX_PATTERN.matcher(name);
        if (matcher.find()) {
            name = matcher.replaceAll("");
        }
        String suffix = String.format(THREAD_NAME_SUFFIX_FORMAT, id);
        thread.setName(name + suffix);
    }

    private String genRequestLog(String remoteAddr, String uri, Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        return sb.append("remote = ").append(remoteAddr).append(SEPARATOR_CHAR).append(StringUtils.SPACE)
                .append("uri = ").append(uri).append(SEPARATOR_CHAR).append(StringUtils.SPACE)
                .append("params = ").append(JSON.toJSONString(params))
                .toString();
    }

    private String genResponseLog(Object ret) {
        StringBuilder sb = new StringBuilder();
        if (ret instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) ret;
            Object body = responseEntity.getBody();
            int statusCodeValue = responseEntity.getStatusCodeValue();
            String reasonPhrase = responseEntity.getStatusCode().getReasonPhrase();
            return sb.append("status = ").append(reasonPhrase).append(SEPARATOR_CHAR).append(StringUtils.SPACE)
                    .append("code = ").append(statusCodeValue).append(SEPARATOR_CHAR).append(StringUtils.SPACE)
                    .append("ret = ").append(JSON.toJSONString(body))
                    .toString();
        } else {
            return sb.append("ret = ").append(ret)
                    .toString();
        }

    }

    private String read(InputStream in, Charset charset) throws IOException {
        StringWriter writer = new StringWriter(BUFFER_SIZE);
        copy(new InputStreamReader(in, charset), writer);
        return writer.toString();
    }

    private int copy(Reader in, Writer out) throws IOException {
        int count = 0;
        char[] buffer = new char[BUFFER_SIZE];
        int length;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
            count += length;
        }
        out.flush();
        return count;
    }

}
