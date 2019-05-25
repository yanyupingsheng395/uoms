package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class TargetDismantVO {

    private String periodType;

    private String periodDate;

    private Double actualVal;

    private String computeDt;

    private Long tgtId;

    private Double tgtVal;

    private Double tgtPercent;
}
