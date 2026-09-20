package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ReviewSubmitDTO;
import com.sky.entity.Review;
import com.sky.mapper.ReviewMapper;
import com.sky.result.PageResult;
import com.sky.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    public void submit(ReviewSubmitDTO dto) {
        Review review = Review.builder()
            .orderId(dto.getOrderId())
            .userId(BaseContext.getCurrentId())
            .rating(dto.getRating())
            .content(dto.getContent())
            .createTime(LocalDateTime.now())
            .build();
        reviewMapper.insert(review);
    }

    @Override
    public List<Review> myReviews() {
        return reviewMapper.getByUserId(BaseContext.getCurrentId());
    }

    @Override
    public PageResult pageQuery(int page, int pageSize) {
        int total = reviewMapper.count();
        List<Review> records = reviewMapper.pageQuery((page - 1) * pageSize, pageSize);
        return new PageResult(total, records);
    }
}