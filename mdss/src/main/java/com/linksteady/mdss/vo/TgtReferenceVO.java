package com.linksteady.mdss.vo;

import lombok.Data;

@Data
public class TgtReferenceVO {
    /**
     * 周期值
     */
    private String period;

    /**
     * 指标值
     */
    private String kpi;

    /**
     *同比
     */
    private String yearOnYear;

    /**
     * 环比
     */
    private String yearOverYear;

}
