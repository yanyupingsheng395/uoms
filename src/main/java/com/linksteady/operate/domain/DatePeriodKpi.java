package com.linksteady.operate.domain;

import java.math.BigDecimal;

/**
 * Created by hxcao on 2019-05-11
 */
public class DatePeriodKpi {

    private String minPeriod;
    private String buyPeriod;
    private String kpiValue;
    private Double retention;

    public String getMinPeriod() {
        return minPeriod;
    }

    public DatePeriodKpi(String minPeriod, String buyPeriod, String kpiValue) {
        this.minPeriod = minPeriod;
        this.buyPeriod = buyPeriod;
        this.kpiValue = kpiValue;
    }

    public DatePeriodKpi(BigDecimal minPeriod, BigDecimal buyPeriod, BigDecimal kpiValue) {
        this.minPeriod = minPeriod.toString();
        this.buyPeriod = buyPeriod.toString();
        this.kpiValue = kpiValue.toString();
    }

    public void setMinPeriod(String minPeriod) {
        this.minPeriod = minPeriod;
    }

    public String getBuyPeriod() {
        return buyPeriod;
    }

    public void setBuyPeriod(String buyPeriod) {
        this.buyPeriod = buyPeriod;
    }

    public String getKpiValue() {
        return kpiValue;
    }

    public void setKpiValue(String kpiValue) {
        this.kpiValue = kpiValue;
    }

    public Double getRetention() {
        return retention;
    }

    public void setRetention(Double retention) {
        this.retention = retention;
    }
}
