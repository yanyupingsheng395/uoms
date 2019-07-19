package com.linksteady.mdss.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "UO_DIAG_DETAIL")
public class DiagDetail implements Serializable {
    /**
     * 诊断ID
     */
    @Column(name = "DIAG_ID")
    private Long diagId;

    /**
     * 节点ID 如果为-1表示根节点
     */
    @Column(name = "NODE_ID")
    private Long nodeId;

    /**
     * 父节点ID
     */
    @Column(name = "PARENT_ID")
    private Long parentId;

    /**
     * 节点名称
     */
    @Column(name = "NODE_NAME")
    private String nodeName;

    /**
     * 指标编码
     */
    @Column(name = "KPI_CODE")
    private String kpiCode;

    /**
     * 指标等级ID
     */
    @Column(name = "KPI_LEVEL_ID")
    private Long kpiLevelId;

    /**
     * 是否标记为问题
     */
    @Column(name = "ALARM_FLAG")
    private String alarmFlag;

    /**
     * 指标名称
     */
    @Column(name = "KPI_NAME")
    private String kpiName;

    @Transient
    private List<DiagCondition> condition;

    public Long getDiagId() {
        return diagId;
    }

    public void setDiagId(Long diagId) {
        this.diagId = diagId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public Long getKpiLevelId() {
        return kpiLevelId;
    }

    public void setKpiLevelId(Long kpiLevelId) {
        this.kpiLevelId = kpiLevelId;
    }

    public String getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(String alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public List<DiagCondition> getCondition() {
        return condition;
    }

    public void setCondition(List<DiagCondition> condition) {
        this.condition = condition;
    }
}