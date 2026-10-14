package com.sky.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Component
@Slf4j
public class DistributedLockUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 尝试加锁
     * @param key 锁key
     * @param timeout 锁超时时间
     * @param unit 时间单位
     * @return 锁的value（解锁时需要），加锁失败返回null
     */
    public String tryLock(String key, long timeout, TimeUnit unit) {
        String value = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        if (Boolean.TRUE.equals(success)) {
            return value;
        }
        return null;
    }

    /**
     * 释放锁（校验value，防止误删）
     */
    public void unlock(String key, String value) {
        if (value == null) return;
        Object current = redisTemplate.opsForValue().get(key);
        if (value.equals(current)) {
            redisTemplate.delete(key);
        }
    }
}