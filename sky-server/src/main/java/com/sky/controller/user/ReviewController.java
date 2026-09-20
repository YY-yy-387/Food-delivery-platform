package com.sky.controller.user;

import com.sky.dto.ReviewSubmitDTO;
import com.sky.entity.Review;
import com.sky.result.Result;
import com.sky.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/review")
@Api(tags = "用户端评价接口")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/submit")
    @ApiOperation("提交评价")
    public Result submit(@RequestBody ReviewSubmitDTO dto) {
        reviewService.submit(dto);
        return Result.success();
    }

    @GetMapping("/my")
    @ApiOperation("我的评价列表")
    public Result<List<Review>> my() {
        return Result.success(reviewService.myReviews());
    }
}