package com.sky.mapper;

import com.sky.entity.PaymentRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {
    @Insert("INSERT INTO payment_record(order_no, order_id, user_id, amount, pay_method, trade_no, status, create_time) VALUES(#{orderNo}, #{orderId}, #{userId}, #{amount}, #{payMethod}, #{tradeNo}, #{status}, #{createTime})")
    void insert(PaymentRecord record);

    @Select("SELECT * FROM payment_record WHERE order_no = #{orderNo}")
    List<PaymentRecord> getByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM payment_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PaymentRecord> getByUserId(@Param("userId") Long userId);
}