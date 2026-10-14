package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review implements Serializable {
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
}