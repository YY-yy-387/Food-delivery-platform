package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkbenchService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台
 */
@RestController("adminWorkbenchController")
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@Slf4j
@RequiredArgsConstructor
public class WorkbenchController {

    private final WorkbenchService workbenchService;

    /**
     * 今日数据
     */
    @GetMapping("/businessData")
    @ApiOperation("今日数据")
    public Result<BusinessDataVO> todayData() {
        BusinessDataVO businessDataVO = workbenchService.getBusinessData();
        return Result.success(businessDataVO);
    }

    /**
     * 订单管理数据
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("订单管理数据")
    public Result<OrderOverViewVO> overviewOrders() {
        OrderOverViewVO orderOverViewVO = workbenchService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }

    /**
     * 菜品总览
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("菜品总览")
    public Result<DishOverViewVO> overviewDishes() {
        DishOverViewVO dishOverViewVO = workbenchService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    /**
     * 套餐总览
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals() {
        SetmealOverViewVO setmealOverViewVO = workbenchService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }
}