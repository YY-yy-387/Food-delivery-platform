package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据概览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVO implements Serializable {

    private Double turnover;//营业额

    private Integer validOrder;//有效订单数

    private Double orderCompletion;//订单完成率

    private Double unitPrice;//平均客单价

    private Integer newUsers;//新增用户数

}
