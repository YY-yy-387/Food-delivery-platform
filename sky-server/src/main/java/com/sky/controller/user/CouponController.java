package com.sky.controller.user;

import com.sky.entity.Coupon;
import com.sky.entity.UserCoupon;
import com.sky.result.Result;
import com.sky.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/coupon")
@Api(tags = "用户端优惠券接口")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/list")
    @ApiOperation("可领取优惠券列表")
    public Result<List<Coupon>> list() {
        return Result.success(couponService.listAvailable());
    }

    @PostMapping("/receive/{id}")
    @ApiOperation("领取优惠券")
    public Result receive(@PathVariable Long id) {
        couponService.receive(id);
        return Result.success();
    }

    @GetMapping("/my")
    @ApiOperation("我的优惠券")
    public Result<List<UserCoupon>> my(@RequestParam(required = false) Integer status) {
        if (status == null) status = 1;
        return Result.success(couponService.myCoupons(status));
    }
}