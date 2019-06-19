package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.*;

/**
 *年度执行情况
 * @author gennerate
 */
@Table(name = "UO_YEAR_HISTORY")
@Data
public class YearHistory {
    /**
     * 年ID
     */
    @Column(name = "YEAR_ID")
    private Long yearId;

    /**
     * gmv的值
     */
    @Column(name = "GMV_VALUE")
    private Long gmvValue;

    /**
     * 相比上年增长率
     */
    @Column(name = "GMV_RATE")
    private Double gmvRate;

}