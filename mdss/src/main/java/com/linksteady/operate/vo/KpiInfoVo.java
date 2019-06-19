package com.linksteady.operate.vo;

import lombok.Data;

/**
 * Created by hxcao on 2019-06-04
 */
@Data
public class KpiInfoVo {

    /**
     * 指标值
     */
    private Double kpiVal;

    /**
     * 时间周期类型
     */
    private String kpiDate;

    /**
     * 复购指标值
     */
    private Double spKpiVal;

    /**
     * 首购指标值
     */
    private Double fpKpiVal;
}
