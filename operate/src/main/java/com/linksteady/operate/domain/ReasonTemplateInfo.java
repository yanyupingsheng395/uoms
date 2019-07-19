package com.linksteady.operate.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReasonTemplateInfo {
    private String templateCode;

    private String templateName;

    private String reasonKpiCode;

    private String reasonKpiName;

    private BigDecimal reasonKpiOrder;

    private BigDecimal templateOrder;

}