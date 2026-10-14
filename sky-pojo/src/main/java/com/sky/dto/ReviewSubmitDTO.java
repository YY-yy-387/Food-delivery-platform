package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ReviewSubmitDTO implements Serializable {
    private Long orderId;
    private Integer rating;
    private String content;
}