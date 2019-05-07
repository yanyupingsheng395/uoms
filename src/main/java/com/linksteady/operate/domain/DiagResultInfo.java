package com.linksteady.operate.domain;

import com.linksteady.operate.vo.DiagConditionVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 诊断的返回结果
 */
@Data
public class DiagResultInfo  implements Serializable {

    /**
     * 诊断ID
     */
    int diagId;

    /**
     * 等级ID
     */
    int kpiLevelId;

    /**
     * 诊断周期类型
     */
    String periodType;

    /**
     *开始时间
     */
    String beginDt;

    /**
     *结束时间
     */
    String endDt;

    /**
     *操作描述 gmv按XXX做加法分析； GMV 按 XX*XX 拆分；
     */
    String handleDesc;

    /**
     *操作类型
     */
    String handleType;

    /**
     *指标编码
     */
    String kpiCode;

    /**
     *指标名称
     */
    String kpiName;

    /**
     *条件信息
     */
    List<DiagConditionVO> whereinfo;

    /**
     * 指标值
     */
    String kpiValue="0";

}
