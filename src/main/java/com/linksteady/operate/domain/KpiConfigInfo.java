package com.linksteady.operate.domain;

import javax.persistence.*;
import lombok.Data;

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

    @Column(name = "DIAG_FLAG")
    private String diagFlag;

    @Column(name = "DISMANT_FORMULA")
    private String dismantFormula;
}