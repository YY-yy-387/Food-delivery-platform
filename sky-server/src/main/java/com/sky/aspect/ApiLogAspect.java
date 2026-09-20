package com.sky.aspect;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口日志切面：自动记录每个Controller接口的URL、入参、出参、耗时
 */
@Aspect
@Component
@Slf4j
public class ApiLogAspect {

    @Pointcut("execution(* com.sky.controller..*.*(..))")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String url = "";
        String method = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            url = request.getRequestURI();
            method = request.getMethod();
        }
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String params = "";
        try {
            params = JSON.toJSONString(args);
            if (params.length() > 500) params = params.substring(0, 500) + "...";
        } catch (Exception e) {
            params = "[无法序列化]";
        }
        log.info("[接口开始] {} {} | {}.{} | 入参: {}", method, url, className, methodName, params);

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            String resultStr = "";
            try {
                resultStr = JSON.toJSONString(result);
                if (resultStr.length() > 300) resultStr = resultStr.substring(0, 300) + "...";
            } catch (Exception e) {
                resultStr = "[无法序列化]";
            }
            if (cost > 1000) {
                log.warn("[接口结束-慢] {} {} | 耗时:{}ms | 出参: {}", method, url, cost, resultStr);
            } else {
                log.info("[接口结束] {} {} | 耗时:{}ms | 出参: {}", method, url, cost, resultStr);
            }
        }
    }
}