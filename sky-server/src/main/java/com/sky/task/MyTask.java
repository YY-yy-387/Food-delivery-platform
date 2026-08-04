package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyTask {

    private final OrderMapper orderMapper;
    /*@Scheduled(cron = "0/5 * * * * ?")
    public void executeTask(){
        log.info("执行定时任务");
    }*/

    @Scheduled(cron = "0 0 1 * * ?")
    public void processOrderTimeout(){
        log.info("处理超时订单");

    }
}
