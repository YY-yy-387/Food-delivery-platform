package com.sky.service;

import com.sky.dto.ReviewSubmitDTO;
import com.sky.entity.Review;
import com.sky.result.PageResult;

import java.util.List;

public interface ReviewService {
    void submit(ReviewSubmitDTO dto);
    List<Review> myReviews();
    PageResult pageQuery(int page, int pageSize);
}