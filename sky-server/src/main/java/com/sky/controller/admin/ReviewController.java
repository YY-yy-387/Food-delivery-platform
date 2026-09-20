package com.sky.controller.admin;

import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("adminReviewController")
@RequestMapping("/admin/review")
@Api(tags = "商家端评价管理")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/page")
    @ApiOperation("评价分页查询")
    public Result<PageResult> page(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(reviewService.pageQuery(page, pageSize));
    }
}