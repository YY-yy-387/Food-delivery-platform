package com.sky.service;

import com.sky.entity.Coupon;
import com.sky.entity.UserCoupon;

import java.util.List;

public interface CouponService {
    List<Coupon> listAvailable();
    void receive(Long couponId);
    List<UserCoupon> myCoupons(Integer status);
    List<Coupon> listAll();
    void updateStatus(Long id, Integer status);
}