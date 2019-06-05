package com.linksteady.operate.vo;

import lombok.Data;

/**
 * Created by hxcao on 2019-06-04
 */
@Data
public class KpiInfoVo {

    private Double kpiVal;

    private String kpiDate;

    private Double spKpiVal; // 复购值

    private Double fpKpiVal; // 首购值
}
