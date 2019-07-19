package com.linksteady.mdss.domain;

import lombok.Data;

@Data
public class ReasonKpisSnp {

    String reasonId;

    String templateCode;

    String reasonKpiCode;

    String reasonKpiName;

    String templateName;

    String templateOrder;

    String reasonKpiOrder;

    double relateValue;

    String relateFlag;
}
