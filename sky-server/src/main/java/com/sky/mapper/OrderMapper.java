package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    /**
     * 根据状态查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("selct * from orders where status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, Long orderTime);
}
