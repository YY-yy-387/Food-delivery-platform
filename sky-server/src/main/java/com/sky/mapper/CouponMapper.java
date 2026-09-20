package com.sky.mapper;

import com.sky.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CouponMapper {
    @Select("SELECT * FROM coupon WHERE status = 1 AND start_time <= NOW() AND end_time >= NOW()")
    List<Coupon> listAvailable();

    @Select("SELECT * FROM coupon WHERE id = #{id}")
    Coupon getById(@Param("id") Long id);

    @Select("SELECT * FROM coupon ORDER BY create_time DESC")
    List<Coupon> listAll();

    @Update("UPDATE coupon SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}