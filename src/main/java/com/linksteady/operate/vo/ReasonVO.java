package com.linksteady.operate.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ReasonVO implements Serializable {

    /**
     * 指标ID
     */
    String kpi;

    String kpiName;
    String beginDt;
    String endDt;
    String period;
    String source;
    /**
     * 维度名称
     */
    String [] dims;
    /**
     * 维度显示信息的列表
     */
    String [] dimDisplay;

    String reasonName;

}
