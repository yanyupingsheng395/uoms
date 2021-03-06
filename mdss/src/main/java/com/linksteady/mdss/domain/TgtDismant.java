package com.linksteady.mdss.domain;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Table(name = "UO_TGT_DISMANT")
public class TgtDismant {
    /**
     * ID
     */
    @Column(name = "ID")
    private Long id;

    /**
     * 目标周期
     */
    @Column(name = "PERIOD_TYPE")
    private String periodType;

    /**
     * 具体周期日期
     */
    @Column(name = "PERIOD_DATE")
    private String periodDate;

    /**
     * 实际值
     */
    @Column(name = "ACTUAL_VAL")
    private Double actualVal;

    /**
     * 计算日期
     */
    @Column(name = "COMPUTE_DT")
    private Date computeDt;

    /**
     * 目标ID
     */
    @Column(name = "TGT_ID")
    private Long tgtId;

    /**
     * 目标值
     */
    @Column(name = "TGT_VAL")
    private Double tgtVal;

    /**
     * 目标占比
     */
    @Column(name = "TGT_PERCENT")
    private Double tgtPercent;

    /**
     * 目标权重指数
     */
    @Column(name = "TGT_WEIGHT_IDX")
    private Double tgtWeightIdx;

    /**
     * 去年指标值
     */
    @Column(name = "ACTUAL_VAL_LAST")
    private Double actualValLast;

    /**
     * 环比增长率
     */
    @Column(name = "GROWTH_RATE")
    private Double growthRate;


    /**
     * 时间窗口是否已过去
     */
    @Column(name = "PAST_FLAG")
    private String pastFlag;

    /**
     * 是否已完成
     */
    @Column(name = "FINISH_FLAG")
    private String finishFlag;

}