package com.linksteady.operate.domain;

import javax.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "UO_TGT_LIST")
public class TargetList implements Serializable {
    /**
     * 目标ID
     */
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
     * 状态（0：停用，1：启用）
     */
    @Column(name = "STATUS")
    private String status;

    @Transient
    private List<TargetDimension> dimensionList;

    @Transient
    private String kpiName;

    @Column(name="TARGET_VAL")
    private String targetVal;
}