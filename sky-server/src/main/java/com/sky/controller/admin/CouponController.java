package com.sky.controller.admin;

import com.sky.entity.Coupon;
import com.sky.result.Result;
import com.sky.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCouponController")
@RequestMapping("/admin/coupon")
@Api(tags = "商家端优惠券管理")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/list")
    @ApiOperation("优惠券列表")
    public Result<List<Coupon>> list() {
        return Result.success(couponService.listAll());
    }

    @PutMapping("/status/{id}/{status}")
    @ApiOperation("启用/禁用优惠券")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        couponService.updateStatus(id, status);
        return Result.success();
    }
}