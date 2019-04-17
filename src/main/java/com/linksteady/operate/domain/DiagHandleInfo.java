package com.linksteady.operate.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 诊断操作描述的表
 */
public class DiagHandleInfo implements Serializable {

    int diagId;  //诊断ID

    int kpiLevelId;  //等级ID

    String periodType; //诊断周期类型

    String beginDt; //开始时间

    String endDt; //结束时间

    String handleDesc;  //操作描述 gmv按XXX做加法分析； GMV 按 XX*XX 拆分；

    String tabName;  //标签名称 预留字段 暂时不用

    String handleType;  //操作类型  M乘法 A加法 F过滤(无操作)

    String templateName; //模板名称

    String nodeId;  //节点ID 在那个节点上进行的新增操作，就存那个节点的ID

    String formula1;  //乘法公式的乘数1

    String formula2;  //乘法公式的乘数2

    String kpiCode;   //指标编码 (也就是拆分的那个指标)

    String addDimCode;  //加法按那个维度进行拆分

    String addDimValues;  //加法拆分的维度值

    //条件信息 list中每一条记录：select t.dim_code,t.dim_name,t.dim_values,t.dim_value_display,t.inherit_flag from UO_DIAG_CONDITION t
    List<Map<String,String>> whereinfo;


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

    public String getHandleDesc() {
        return handleDesc;
    }

    public void setHandleDesc(String handleDesc) {
        this.handleDesc = handleDesc;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getFormula1() {
        return formula1;
    }

    public void setFormula1(String formula1) {
        this.formula1 = formula1;
    }

    public String getFormula2() {
        return formula2;
    }

    public void setFormula2(String formula2) {
        this.formula2 = formula2;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public String getAddDimCode() {
        return addDimCode;
    }

    public void setAddDimCode(String addDimCode) {
        this.addDimCode = addDimCode;
    }

    public String getAddDimValues() {
        return addDimValues;
    }

    public void setAddDimValues(String addDimValues) {
        this.addDimValues = addDimValues;
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

    public List<Map<String, String>> getWhereinfo() {
        return whereinfo;
    }

    public void setWhereinfo(List<Map<String, String>> whereinfo) {
        this.whereinfo = whereinfo;
    }
}
