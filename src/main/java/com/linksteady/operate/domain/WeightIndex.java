package com.linksteady.operate.domain;

import javax.persistence.*;

@Table(name = "UO_WEIGHT_INDEX")
public class WeightIndex {
    /**
     * 年ID
     */
    @Column(name = "YEAR_ID")
    private Long yearId;

    /**
     * 月份ID
     */
    @Column(name = "MONTH_ID")
    private Long monthId;

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

    public Long getYearId() {
        return yearId;
    }

    public void setYearId(Long yearId) {
        this.yearId = yearId;
    }

    public Long getMonthId() {
        return monthId;
    }

    public void setMonthId(Long monthId) {
        this.monthId = monthId;
    }

    public Double getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Double indexValue) {
        this.indexValue = indexValue;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }
}