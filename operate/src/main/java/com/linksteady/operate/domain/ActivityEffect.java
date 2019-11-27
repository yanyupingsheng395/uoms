package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-11-27
 */
@Data
public class ActivityEffect {

    private long headId;
    private long effectDt;
    private long pushUcnt;
    private long convertUcnt;
    private double convertRate;
    private double pushRoi;
    private double convertAmount;
    private double convertAmountCspp;
    private double ocConvertAmount;
    private double ocConvertAmountCssp;
    private double amountOcCspp;
    private double convertUcntCspp;
    private long ocConvertUcnt;
    private double ocConvertUcntCssp;
    private double ucntOcCspp;
    private String activityStage;
    private String pushKpi;

    private String kpiName;
    private String kpiVal;
    private String allStage;
    private String preheatStage;
    private String normalStage;
    private String mainKpi;
}
