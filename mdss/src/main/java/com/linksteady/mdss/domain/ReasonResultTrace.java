package com.linksteady.mdss.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReasonResultTrace {

    private BigDecimal reasonResultId;

    private BigDecimal reasonId;

    private String reasonName;

    private String kpiCode;

    private String kpiName;

    private String periodType;

    private String beginDt;

    private String endDt;

    private String reasonCode;

    private String formulaDesc;

    private String formula;

    private String business;

    private Date createDt;

    private String dimDisplayName;
}
