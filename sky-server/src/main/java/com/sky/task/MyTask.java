package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyTask {

    private final OrderMapper orderMapper;

    /**
     * 处理超时订单：下单超过15分钟仍未支付的订单自动取消
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void processOrderTimeout() {
        log.info("处理超时订单: {}", LocalDateTime.now());

        // 查询下单时间超过15分钟且状态为待付款的订单
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
                log.info("已自动取消超时订单：{}", orders.getNumber());
            }
        }
    }
}