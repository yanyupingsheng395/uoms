package com.linksteady.operate.domain;

import lombok.Data;

import java.math.BigDecimal;
import javax.persistence.*;
@Data
public class ReasonTemplateInfo {
    private String templateCode;

    private String templateName;

    private String reasonKpiCode;

    private String reasonKpiName;

    private BigDecimal reasonKpiOrder;

    private BigDecimal templateOrder;

}