package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord implements Serializable {
    private Long id;
    private String orderNo;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private Integer payMethod;
    private String tradeNo;
    private Integer status;
    private LocalDateTime createTime;
}