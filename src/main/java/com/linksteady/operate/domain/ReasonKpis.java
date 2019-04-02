package com.linksteady.operate.domain;

import java.io.Serializable;
import java.util.Date;

public class ReasonKpis implements Serializable {

   int reasonId;
   String templateCode;
   String reasonKpiCode;
   String reasonKpiType;
   String reasonKpiName;
   Date beginDate;
   Date endDate;
   String periodType;
   String chartType;
   String configInfo2;
   byte[] datas;
   String result;

   Date createDt;
   Date updateDt;

   String datasstr;  //仅供查询时使用

    public int getReasonId() {
        return reasonId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getReasonKpiCode() {
        return reasonKpiCode;
    }

    public void setReasonKpiCode(String reasonKpiCode) {
        this.reasonKpiCode = reasonKpiCode;
    }

    public String getReasonKpiType() {
        return reasonKpiType;
    }

    public void setReasonKpiType(String reasonKpiType) {
        this.reasonKpiType = reasonKpiType;
    }

    public String getReasonKpiName() {
        return reasonKpiName;
    }

    public void setReasonKpiName(String reasonKpiName) {
        this.reasonKpiName = reasonKpiName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getConfigInfo2() {
        return configInfo2;
    }

    public void setConfigInfo2(String configInfo2) {
        this.configInfo2 = configInfo2;
    }

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public Date getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(Date updateDt) {
        this.updateDt = updateDt;
    }

    public String getDatasstr() {
        return datasstr;
    }

    public void setDatasstr(String datasstr) {
        this.datasstr = datasstr;
    }
}
