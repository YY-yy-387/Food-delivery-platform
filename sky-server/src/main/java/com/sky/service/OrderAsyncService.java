package com.sky.service;

import com.alibaba.fastjson.JSON;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 异步下单服务：基于Redis List队列，后台线程消费
 */
@Service
@Slf4j
public class OrderAsyncService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    private static final String QUEUE_KEY = "order:async:queue";
    private static final String RESULT_KEY_PREFIX = "order:async:result:";
    private volatile boolean running = true;
    private Thread consumerThread;

    /**
     * 提交异步下单请求，返回任务ID
     */
    public String submit(OrdersSubmitDTO dto, Long userId) {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> task = new HashMap<>();
        task.put("taskId", taskId);
        task.put("userId", userId);
        task.put("dto", dto);
        redisTemplate.opsForList().rightPush(QUEUE_KEY, JSON.toJSONString(task));
        log.info("[异步下单] 任务已入队 taskId={}, userId={}", taskId, userId);
        return taskId;
    }

    /**
     * 查询异步下单结果
     */
    public OrderSubmitVO getResult(String taskId) {
        Object obj = redisTemplate.opsForValue().get(RESULT_KEY_PREFIX + taskId);
        if (obj == null) return null;
        return JSON.parseObject(obj.toString(), OrderSubmitVO.class);
    }

    @PostConstruct
    public void init() {
        consumerThread = new Thread(() -> {
            log.info("[异步下单] 消费线程启动");
            while (running) {
                try {
                    Object taskObj = redisTemplate.opsForList().leftPop(QUEUE_KEY, 2, TimeUnit.SECONDS);
                    if (taskObj == null) continue;
                    Map<String, Object> task = JSON.parseObject(taskObj.toString(), Map.class);
                    String taskId = (String) task.get("taskId");
                    Long userId = Long.valueOf(task.get("userId").toString());
                    OrdersSubmitDTO dto = JSON.parseObject(JSON.toJSONString(task.get("dto")), OrdersSubmitDTO.class);
                    log.info("[异步下单] 开始处理 taskId={}, userId={}", taskId, userId);
                    // 用ThreadLocal设置用户ID（因为是新线程，需要手动设置）
                    com.sky.context.BaseContext.setCurrentId(userId);
                    try {
                        OrderSubmitVO result = orderService.submitOrder(dto);
                        redisTemplate.opsForValue().set(RESULT_KEY_PREFIX + taskId, JSON.toJSONString(result), 30, TimeUnit.MINUTES);
                        log.info("[异步下单] 处理成功 taskId={}, orderId={}", taskId, result.getId());
                    } catch (Exception e) {
                        log.error("[异步下单] 处理失败 taskId={}", taskId, e);
                        Map<String, Object> err = new HashMap<>();
                        err.put("error", e.getMessage());
                        redisTemplate.opsForValue().set(RESULT_KEY_PREFIX + taskId, JSON.toJSONString(err), 30, TimeUnit.MINUTES);
                    } finally {
                        com.sky.context.BaseContext.removeCurrentId();
                    }
                } catch (Exception e) {
                    log.error("[异步下单] 消费线程异常", e);
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
            log.info("[异步下单] 消费线程停止");
        }, "order-async-consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    @PreDestroy
    public void destroy() {
        running = false;
        if (consumerThread != null) {
            consumerThread.interrupt();
        }
    }
}