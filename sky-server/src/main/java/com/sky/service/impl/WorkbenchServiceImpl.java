package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkbenchService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkbenchServiceImpl implements WorkbenchService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     */
    @Override
    public BusinessDataVO getBusinessData() {
        // 获得今天的时间范围
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);
        return getBusinessData(beginTime, endTime);
    }

    /**
     * 查询指定时间段内的运营数据
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
        // 查询时间段内总营业额（仅统计已完成订单）
        Map<String, Object> map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        // 查询时间段内总订单数
        map.put("status", null);
        Integer orderCount = orderMapper.countByMap(map);

        // 查询时间段内有效订单数（已完成）
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = orderMapper.countByMap(map);

        // 查询时间段内新增用户数
        map.put("status", null);
        Integer newUsers = userMapper.countByMap(map);

        // 计算订单完成率
        Double orderCompletionRate = 0.0;
        if (orderCount != null && orderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / orderCount;
        }

        // 计算平均客单价
        Double unitPrice = 0.0;
        if (turnover != 0) {
            unitPrice = turnover / validOrderCount;
        }

        BusinessDataVO businessDataVO = new BusinessDataVO();
        businessDataVO.setTurnover(turnover);
        businessDataVO.setValidOrder(validOrderCount);
        businessDataVO.setOrderCompletion(orderCompletionRate);
        businessDataVO.setUnitPrice(unitPrice);
        businessDataVO.setNewUsers(newUsers);

        return businessDataVO;
    }

    /**
     * 查询订单管理数据
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
        // 待接单（状态2）
        orderOverViewVO.setWaitingOrders(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));
        // 待派送（状态3）
        orderOverViewVO.setDeliveredOrders(orderMapper.countStatus(Orders.CONFIRMED));
        // 已完成（状态5）
        orderOverViewVO.setCompletedOrders(orderMapper.countStatus(Orders.COMPLETED));
        // 已取消（状态6）
        orderOverViewVO.setCancelledOrders(orderMapper.countStatus(Orders.CANCELLED));
        // 全部订单
        orderOverViewVO.setAllOrders(orderMapper.countByMap(new HashMap<>()));
        return orderOverViewVO;
    }

    /**
     * 查询菜品总览
     */
    @Override
    public DishOverViewVO getDishOverView() {
        DishOverViewVO dishOverViewVO = new DishOverViewVO();
        // 起售中的菜品数量
        dishOverViewVO.setSold(dishMapper.countByStatus(StatusConstant.ENABLE));
        // 停售中的菜品数量
        dishOverViewVO.setDiscontinued(dishMapper.countByStatus(StatusConstant.DISABLE));
        return dishOverViewVO;
    }

    /**
     * 查询套餐总览
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        SetmealOverViewVO setmealOverViewVO = new SetmealOverViewVO();
        // 起售中的套餐数量
        setmealOverViewVO.setSold(setmealMapper.countByStatus(StatusConstant.ENABLE));
        // 停售中的套餐数量
        setmealOverViewVO.setDiscontinued(setmealMapper.countByStatus(StatusConstant.DISABLE));
        return setmealOverViewVO;
    }
}