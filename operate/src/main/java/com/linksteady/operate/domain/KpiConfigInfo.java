package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "UO_KPI_LIST_CONFIG")
public class KpiConfigInfo {
    @Column(name = "KPI_CODE")
    private String kpiCode;

    @Column(name = "KPI_NAME")
    private String kpiName;

    @Column(name = "REASON_FLAG")
    private String reasonFlag;

    @Column(name = "DISMANT_FLAG")
    private String dismantFlag;

    @Column(name = "DISMANT_FORMULA")
    private String dismantFormula;

    @Column(name = "AXIS_NAME")
    private String axisName;

    @Column(name = "VALUE_FORMAT")
    private String  valueFormat;
}