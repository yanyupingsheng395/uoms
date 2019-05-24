package com.linksteady.operate.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "UO_TGT_MONITOR")
public class TgtMonitor {
    /**
     * ID
     */
    @Column(name = "ID")
    private Long id;

    /**
     * 周期日期
     */
    @Column(name = "PERIOD_DATE")
    private String periodDate;

    /**
     * 数据类型
     */
    @Column(name = "DATA_TYPE")
    private String dataType;

    /**
     * 实际值
     */
    @Column(name = "ACTUAL_VAL")
    private Double actualVal;

    /**
     * 目标值
     */
    @Column(name = "TGT_VAL")
    private Double tgtVal;

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
     * 计算日期
     */
    @Column(name = "COMPUTE_DT")
    private Date computeDt;

    /**
     * 目标ID
     */
    @Column(name = "TGT_ID")
    private Double tgtId;
}