package com.sky.exception;

/**
 * 重复提交异常
 */
public class RepeatSubmitException extends BaseException {
    public RepeatSubmitException(String msg) {
        super(msg);
    }
}