package com.linksteady.mdss.domain;

import java.io.Serializable;

/**
 * 月度关键指标完成情况
 */
public class KeyPointYear implements Serializable {
    int yearId;

    double gmvActual;
    double gmvTarget;
    double gmvCmpRate;
    double getGmvCmpTotalRate;
    double timeRate;

    double gmvPreYear;
    double gmvPreYearDiff;
    double gmvPreYearRate;

    double costYear;
    double incomeYear;
    double profitRate;
    double profitAlertRate;
    String  costHealthFlag;
    double volatility;
    int reachNum;
    int unreachNum;
    int ongoingNum;
    int nostartNum;

    public int getYearId() {
        return yearId;
    }

    public void setYearId(int yearId) {
        this.yearId = yearId;
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

    public double getGmvPreYear() {
        return gmvPreYear;
    }

    public void setGmvPreYear(double gmvPreYear) {
        this.gmvPreYear = gmvPreYear;
    }

    public double getGmvPreYearDiff() {
        return gmvPreYearDiff;
    }

    public void setGmvPreYearDiff(double gmvPreYearDiff) {
        this.gmvPreYearDiff = gmvPreYearDiff;
    }

    public double getGmvPreYearRate() {
        return gmvPreYearRate;
    }

    public void setGmvPreYearRate(double gmvPreYearRate) {
        this.gmvPreYearRate = gmvPreYearRate;
    }

    public double getCostYear() {
        return costYear;
    }

    public void setCostYear(double costYear) {
        this.costYear = costYear;
    }

    public double getIncomeYear() {
        return incomeYear;
    }

    public void setIncomeYear(double incomeYear) {
        this.incomeYear = incomeYear;
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

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public int getReachNum() {
        return reachNum;
    }

    public void setReachNum(int reachNum) {
        this.reachNum = reachNum;
    }

    public int getUnreachNum() {
        return unreachNum;
    }

    public void setUnreachNum(int unreachNum) {
        this.unreachNum = unreachNum;
    }

    public int getOngoingNum() {
        return ongoingNum;
    }

    public void setOngoingNum(int ongoingNum) {
        this.ongoingNum = ongoingNum;
    }

    public int getNostartNum() {
        return nostartNum;
    }

    public void setNostartNum(int nostartNum) {
        this.nostartNum = nostartNum;
    }
}
