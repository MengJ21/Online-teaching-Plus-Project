package com.meng.exception;

import java.io.Serializable;

/**
 * @author 梦举
 * @version 1.0
 * @description 错误响应参数包装
 * @date 2023/3/23 11:39
 */

public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}