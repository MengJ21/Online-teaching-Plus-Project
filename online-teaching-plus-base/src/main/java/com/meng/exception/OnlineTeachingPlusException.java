package com.meng.exception;

/**
 * @author 梦举
 * @version 1.0
 * @description 异常类
 * @date 2023/3/23 11:29
 */

public class OnlineTeachingPlusException extends RuntimeException {
    private static final long serialVersionUID = 5565760508056698922L;

    private String errMessage;

    public OnlineTeachingPlusException() {
        super();
    }

    public OnlineTeachingPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String  getErrMessage() {
        return errMessage;
    }
    public static void cast(CommonError commonError) {
        throw new OnlineTeachingPlusException(commonError.getErrMessage());
    }

    public static void cast(String errMessage) {
        throw new OnlineTeachingPlusException(errMessage);
    }
}