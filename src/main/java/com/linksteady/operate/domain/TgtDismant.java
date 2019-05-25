package com.linksteady.operate.domain;

import javax.persistence.*;
import lombok.Data;

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
    private String computeDt;

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

    @Column(name = "TGT_WEIGHT_IDX")
    private Double tgtWeightIdx;
}