package com.linksteady.operate.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 诊断的返回结果
 */
public class DiagResultInfo  implements Serializable {

    int diagId;  //诊断ID
    int kpiLevelId;  //等级ID
    String periodType; //诊断周期类型
    String beginDt; //开始时间
    String endDt; //结束时间
    String handleDesc;  //操作描述 gmv按XXX做加法分析； GMV 按 XX*XX 拆分；
    String handleType;  //操作类型

    String kpiCode;  //指标编码
    String kpiName; //指标名称

    //条件信息
    List<Map<String,String>> whereinfo;

    double kpiValue=0d;


    public int getDiagId() {
        return diagId;
    }

    public void setDiagId(int diagId) {
        this.diagId = diagId;
    }

    public int getKpiLevelId() {
        return kpiLevelId;
    }

    public void setKpiLevelId(int kpiLevelId) {
        this.kpiLevelId = kpiLevelId;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getBeginDt() {
        return beginDt;
    }

    public void setBeginDt(String beginDt) {
        this.beginDt = beginDt;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    public String getHandleDesc() {
        return handleDesc;
    }

    public void setHandleDesc(String handleDesc) {
        this.handleDesc = handleDesc;
    }

    public List<Map<String, String>> getWhereinfo() {
        return whereinfo;
    }

    public void setWhereinfo(List<Map<String, String>> whereinfo) {
        this.whereinfo = whereinfo;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public double getKpiValue() {
        return kpiValue;
    }

    public void setKpiValue(double kpiValue) {
        this.kpiValue = kpiValue;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }
}
