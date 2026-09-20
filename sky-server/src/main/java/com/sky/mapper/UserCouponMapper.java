package com.sky.mapper;

import com.sky.entity.UserCoupon;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserCouponMapper {
    @Insert("INSERT INTO user_coupon(user_id, coupon_id, status, receive_time) VALUES(#{userId}, #{couponId}, 1, NOW())")
    void insert(UserCoupon userCoupon);

    @Select("SELECT * FROM user_coupon WHERE user_id = #{userId} AND status = #{status}")
    List<UserCoupon> getByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT * FROM user_coupon WHERE id = #{id}")
    UserCoupon getById(@Param("id") Long id);

    @Update("UPDATE user_coupon SET status = 2, order_id = #{orderId}, use_time = NOW() WHERE id = #{id}")
    void useCoupon(@Param("id") Long id, @Param("orderId") Long orderId);

    @Select("SELECT COUNT(*) FROM user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId} AND status = 1")
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
}