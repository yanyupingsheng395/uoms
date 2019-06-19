package com.linksteady.operate.domain;

import javax.persistence.*;
import java.util.Date;

@Table(name = "UO_DIAG_CONDITION")
public class DiagCondition {
    /**
     * 诊断ID
     */
    @Column(name = "DIAG_ID")
    private Long diagId;

    /**
     * 节点ID
     */
    @Column(name = "NODE_ID")
    private Long nodeId;

    /**
     * 维度编码
     */
    @Column(name = "DIM_CODE")
    private String dimCode;

    /**
     * 维度名称
     */
    @Column(name = "DIM_NAME")
    private String dimName;

    /**
     * 维度值
     */
    @Column(name = "DIM_VALUES")
    private String dimValues;

    /**
     * 维度显示值
     */
    @Column(name = "DIM_VALUE_DISPLAY")
    private String dimValueDisplay;

    @Column(name = "INHERIT_FLAG")
    private String inheritFlag;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_DT")
    private Date createDt;

    /**
     * 获取诊断ID
     *
     * @return DIAG_ID - 诊断ID
     */
    public Long getDiagId() {
        return diagId;
    }

    /**
     * 设置诊断ID
     *
     * @param diagId 诊断ID
     */
    public void setDiagId(Long diagId) {
        this.diagId = diagId;
    }

    /**
     * 获取节点ID
     *
     * @return NODE_ID - 节点ID
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * 设置节点ID
     *
     * @param nodeId 节点ID
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * 获取维度编码
     *
     * @return DIM_CODE - 维度编码
     */
    public String getDimCode() {
        return dimCode;
    }

    /**
     * 设置维度编码
     *
     * @param dimCode 维度编码
     */
    public void setDimCode(String dimCode) {
        this.dimCode = dimCode == null ? null : dimCode.trim();
    }

    /**
     * 获取维度值
     *
     * @return DIM_VALUES - 维度值
     */
    public String getDimValues() {
        return dimValues;
    }

    /**
     * 设置维度值
     *
     * @param dimValues 维度值
     */
    public void setDimValues(String dimValues) {
        this.dimValues = dimValues == null ? null : dimValues.trim();
    }

    /**
     * 获取维度显示值
     *
     * @return DIM_VALUE_DISPLAY - 维度显示值
     */
    public String getDimValueDisplay() {
        return dimValueDisplay;
    }

    /**
     * 设置维度显示值
     *
     * @param dimValueDisplay 维度显示值
     */
    public void setDimValueDisplay(String dimValueDisplay) {
        this.dimValueDisplay = dimValueDisplay == null ? null : dimValueDisplay.trim();
    }

    public String getInheritFlag() {
        return inheritFlag;
    }

    public void setInheritFlag(String inheritFlag) {
        this.inheritFlag = inheritFlag;
    }

    public String getDimName() {
        return dimName;
    }

    public void setDimName(String dimName) {
        this.dimName = dimName;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }
}