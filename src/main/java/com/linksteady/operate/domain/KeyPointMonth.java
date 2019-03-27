package com.linksteady.operate.domain;

import java.io.Serializable;

/**
 * 月度关键指标完成情况
 */
public class KeyPointMonth implements Serializable {

    int monthId;

    double gmvActual;
    double gmvTarget;
    double gmvCmpRate;
    double getGmvCmpTotalRate;
    double timeRate;
    double gmvLastMonth;
    double gmvLastMonthDiff;
    double gmvLastMonthRate;
    double gmvPreMonth;
    double gmvPreMonthDiff;
    double gmvPreMonthRate;

    double costMonth;
    double incomeMonth;
    double profitRate;
    double profitAlertRate;
    String  costHealthFlag;

    public int getMonthId() {
        return monthId;
    }

    public void setMonthId(int monthId) {
        this.monthId = monthId;
    }

    public double getGmvActual() {
        return gmvActual;
    }

    public void setGmvActual(double gmvActual) {
        this.gmvActual = gmvActual;
    }

    public double getGmvTarget() {
        return gmvTarget;
    }

    public void setGmvTarget(double gmvTarget) {
        this.gmvTarget = gmvTarget;
    }

    public double getGmvCmpRate() {
        return gmvCmpRate;
    }

    public void setGmvCmpRate(double gmvCmpRate) {
        this.gmvCmpRate = gmvCmpRate;
    }

    public double getGetGmvCmpTotalRate() {
        return getGmvCmpTotalRate;
    }

    public void setGetGmvCmpTotalRate(double getGmvCmpTotalRate) {
        this.getGmvCmpTotalRate = getGmvCmpTotalRate;
    }

    public double getTimeRate() {
        return timeRate;
    }

    public void setTimeRate(double timeRate) {
        this.timeRate = timeRate;
    }

    public double getGmvLastMonth() {
        return gmvLastMonth;
    }

    public void setGmvLastMonth(double gmvLastMonth) {
        this.gmvLastMonth = gmvLastMonth;
    }

    public double getGmvLastMonthDiff() {
        return gmvLastMonthDiff;
    }

    public void setGmvLastMonthDiff(double gmvLastMonthDiff) {
        this.gmvLastMonthDiff = gmvLastMonthDiff;
    }

    public double getGmvLastMonthRate() {
        return gmvLastMonthRate;
    }

    public void setGmvLastMonthRate(double gmvLastMonthRate) {
        this.gmvLastMonthRate = gmvLastMonthRate;
    }

    public double getGmvPreMonth() {
        return gmvPreMonth;
    }

    public void setGmvPreMonth(double gmvPreMonth) {
        this.gmvPreMonth = gmvPreMonth;
    }

    public double getGmvPreMonthDiff() {
        return gmvPreMonthDiff;
    }

    public void setGmvPreMonthDiff(double gmvPreMonthDiff) {
        this.gmvPreMonthDiff = gmvPreMonthDiff;
    }

    public double getGmvPreMonthRate() {
        return gmvPreMonthRate;
    }

    public void setGmvPreMonthRate(double gmvPreMonthRate) {
        this.gmvPreMonthRate = gmvPreMonthRate;
    }

    public double getCostMonth() {
        return costMonth;
    }

    public void setCostMonth(double costMonth) {
        this.costMonth = costMonth;
    }

    public double getIncomeMonth() {
        return incomeMonth;
    }

    public void setIncomeMonth(double incomeMonth) {
        this.incomeMonth = incomeMonth;
    }

    public double getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(double profitRate) {
        this.profitRate = profitRate;
    }

    public double getProfitAlertRate() {
        return profitAlertRate;
    }

    public void setProfitAlertRate(double profitAlertRate) {
        this.profitAlertRate = profitAlertRate;
    }

    public String getCostHealthFlag() {
        return costHealthFlag;
    }

    public void setCostHealthFlag(String costHealthFlag) {
        this.costHealthFlag = costHealthFlag;
    }
}
