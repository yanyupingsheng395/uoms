package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "UO_WEIGHT_INDEX_CONFIG")
public class WeightIndex {
    /**
     * 周期ID
     */
    @Column(name = "PERIOD_ID")
    private String periodId;

    /**
     * 周期类型
     */
    @Column(name = "PERIOD_TYPE")
    private String periodType;

    /**
     * 权重指数值
     */
    @Column(name = "INDEX_VALUE")
    private Double indexValue;

    /**
     * 权重指数类型 GMV表示GMV的权重指数
     */
    @Column(name = "INDEX_TYPE")
    private String indexType;

}