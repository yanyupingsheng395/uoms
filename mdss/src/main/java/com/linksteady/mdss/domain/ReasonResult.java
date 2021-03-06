package com.linksteady.mdss.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "UO_REASON_RESULT")
@Data
public class ReasonResult {

    @Column(name = "REASON_RESULT_ID")
    private BigDecimal reasonResultId;

    @Column(name = "REASON_ID")
    private BigDecimal reasonId;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "FORMULA_DESC")
    private String formulaDesc;

    @Column(name = "FORMULA")
    private String formula;

    @Column(name = "BUSINESS")
    private String business;

    @Column(name = "CREATE_DT")
    private Date createDt;

    @Column(name = "TRACE_FLAG")
    private String traceFlag;

}