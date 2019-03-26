package com.linksteady.operate.domain;

import javax.persistence.*;

@Table(name = "UO_YEAR_HISTORY")
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
    private Long gmvRate;

    public Long getYearId() {
        return yearId;
    }

    public void setYearId(Long yearId) {
        this.yearId = yearId;
    }

    public Long getGmvValue() {
        return gmvValue;
    }

    public void setGmvValue(Long gmvValue) {
        this.gmvValue = gmvValue;
    }

    public Long getGmvRate() {
        return gmvRate;
    }

    public void setGmvRate(Long gmvRate) {
        this.gmvRate = gmvRate;
    }
}