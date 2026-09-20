package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.Coupon;
import com.sky.entity.UserCoupon;
import com.sky.exception.BaseException;
import com.sky.mapper.CouponMapper;
import com.sky.mapper.UserCouponMapper;
import com.sky.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    public List<Coupon> listAvailable() {
        return couponMapper.listAvailable();
    }

    @Override
    public void receive(Long couponId) {
        Coupon coupon = couponMapper.getById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BaseException("优惠券不存在或已下架");
        }
        Long userId = BaseContext.getCurrentId();
        int count = userCouponMapper.countByUserAndCoupon(userId, couponId);
        if (count > 0) {
            throw new BaseException("已领取过该优惠券");
        }
        UserCoupon uc = UserCoupon.builder().userId(userId).couponId(couponId).build();
        userCouponMapper.insert(uc);
    }

    @Override
    public List<UserCoupon> myCoupons(Integer status) {
        return userCouponMapper.getByUserIdAndStatus(BaseContext.getCurrentId(), status);
    }

    @Override
    public List<Coupon> listAll() {
        return couponMapper.listAll();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        couponMapper.updateStatus(id, status);
    }
}