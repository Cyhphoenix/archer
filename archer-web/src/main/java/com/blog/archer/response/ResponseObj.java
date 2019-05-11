package com.blog.archer.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @author yuanhang
 */
@Data
@Builder
public class ResponseObj implements Serializable {
    private static final long serialVersionUID = -8312098754828420816L;
    private static final int SUCCESS = 0;
    private static final int FAIL = 1;
    private static final String SUCCESS_MSG = "success";
    private static final String FAIL_MSG = "failure";
    private int status;
    private Object data;

    public static Object success() {
        ResponseObj responseObj = ResponseObj.builder()
                .status(SUCCESS)
                .data(SUCCESS_MSG)
                .build();
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }

    public static Object success(Object data) {
        ResponseObj responseObj = ResponseObj.builder()
                .status(SUCCESS)
                .data(data)
                .build();
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }


    public static Object fail() {
        ResponseObj responseObj = ResponseObj.builder()
                .status(FAIL)
                .data(FAIL_MSG)
                .build();
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }

    public static Object fail(Object data) {
        ResponseObj responseObj = ResponseObj.builder()
                .status(FAIL)
                .data(data)
                .build();
        return new ResponseEntity<>(responseObj, HttpStatus.OK);

    }

}
