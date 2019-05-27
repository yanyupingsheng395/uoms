package com.linksteady.operate.domain;

import javax.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "UO_TGT_LIST")
public class TargetInfo implements Serializable {
    /**
     * 目标ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 目标名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 周期类型
     */
    @Column(name = "PERIOD_TYPE")
    private String periodType;

    /**
     * 开始时间
     */
    @Column(name = "START_DT")
    private String startDt;

    /**
     * 结束时间
     */
    @Column(name = "END_DT")
    private String endDt;

    /**
     * 指标编码
     */
    @Column(name = "KPI_CODE")
    private String kpiCode;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_DT")
    private Date createDt;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_DT")
    private Date updateDt;

    /**
     * 创建人
     */
    @Column(name = "CREATE_BY")
    private String createBy;

    /**
     * 修改人
     */
    @Column(name = "UPDATE_BY")
    private String updateBy;

    /**
     * 状态
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 目标值
     */
    @Column(name = "TARGET_VAL")
    private Double targetVal;

    /**
     * 计算日期
     */
    @Column(name = "COMPUTE_DT")
    private Date computeDt;

    /**
     * 实际值
     */
    @Column(name = "ACTUAL_VAL")
    private Double actualVal;

    /**
     * 实际值去年同比
     */
    @Column(name = "ACTUAL_VAL_RATE")
    private Double actualValRate;

    /**
     * 实际值去年同期
     */
    @Column(name = "ACTUAL_VAL_LAST")
    private Double actualValLast;

    /**
     * 完成率
     */
    @Column(name = "FINISH_RATE")
    private Double finishRate;

    /**
     * 完成率差
     */
    @Column(name = "FINISH_RATE_DIFFER")
    private Double finishRateDiffer;

    /**
     * 完成率去年同期
     */
    @Column(name = "FINISH_RATE_LAST")
    private Double finishRateLast;

    /**
     * 指标度量单位
     */
    @Column(name = "KPI_UNIT")
    private String kpiUnit;

    @Transient
    private List<TargetDimension> dimensionList;

    @Transient
    private String kpiName;

    /**
     * 完成率（%）
     */
    @Column(name = "FINISH_DEGREE")
    private Double finishDegree;

    /**
     * 剩余目标
     */
    @Column(name = "REMAIN_TGT")
    private Double remainTgt;

    /**
     * 剩余目标个数
     */
    @Column(name = "REMAIN_COUNT")
    private Integer remainCount;

    /**
     * 剩余目标的日期，多值以逗号隔开
     */
    @Column(name = "REMAIN_LIST")
    private String remainList;

    /**
     * 变异系数（本年迄今为止）
     */
    @Column(name = "VARY_IDX")
    private Double varyIdx;

    /**
     * 变异系数（去年同期）
     */
    @Column(name = "VARY_IDX_LAST")
    private Double varyIdxLast;

    /**
     * 业绩缺口
     */
    @Column(name = "FINISH_DIFFER")
    private Double finishDiffer;

}