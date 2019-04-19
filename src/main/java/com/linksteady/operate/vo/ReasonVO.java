package com.linksteady.operate.vo;

import java.io.Serializable;

public class ReasonVO implements Serializable {

    String kpi;  //指标ID

    String kpiName;  //指标名称
    String startDt;
    String endDt;
    String period;
    String source;
    String [] dims;  //维度名称
    String [] dimDisplay; //维度显示信息的列表

    String reasonName;

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String[] getDims() {
        return dims;
    }

    public void setDims(String[] dims) {
        this.dims = dims;
    }

    public String getKpiName() {
        return kpiName;
    }

     public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public String[] getDimDisplay() {
        return dimDisplay;
    }

    public void setDimDisplay(String[] dimDisplay) {
        this.dimDisplay = dimDisplay;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }
}
