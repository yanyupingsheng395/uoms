package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-11-27
 */
@Data
public class ActivityEffect {

    private String kpiName;
    private Double allStage;

    private Double preheatAll;
    private Double preheatNotify;
    private Double preheatDuring;

    private Double  normalAll;
    private Double  normalNotify;
    private Double normalDuring;

    private Long successNum;
    private Date pushDate;

    public ActivityEffect() {
    }

    public ActivityEffect(String kpiName, Double allStage, Double preheatAll, Double preheatNotify, Double preheatDuring, Double normalAll, Double normalNotify, Double normalDuring) {
        this.kpiName = kpiName;
        this.allStage = allStage;
        this.preheatAll = preheatAll;
        this.preheatNotify = preheatNotify;
        this.preheatDuring = preheatDuring;
        this.normalAll = normalAll;
        this.normalNotify = normalNotify;
        this.normalDuring = normalDuring;
    }
}
