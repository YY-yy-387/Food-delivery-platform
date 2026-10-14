package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解：基于Redis固定窗口，指定时间窗口内最大请求数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /** 时间窗口（秒） */
    int time() default 1;
    /** 窗口内最大请求数 */
    int count() default 10;
    /** 限流提示信息 */
    String message() default "请求过于频繁，请稍后再试";
}