package com.linksteady.operate.domain;

import javax.persistence.*;

@Table(name = "UO_DIAG_LIST")
public class Diag {
    /**
     * 主键ID
     */
    @Column(name = "DIAG_ID")
    private Long diagId;

    /**
     * 诊断名称
     */
    @Column(name = "DIAG_NAME")
    private String diagName;

    /**
     * 周期类型 M表示按月 Y表示按天
     */
    @Column(name = "PERIOD_TYPE")
    private String periodType;

    /**
     * 周期开始时间
     */
    @Column(name = "BEGIN_DT")
    private String beginDt;

    /**
     * 周期结束时间
     */
    @Column(name = "END_DT")
    private String endDt;

    /**
     * 获取主键ID
     *
     * @return DIAG_ID - 主键ID
     */
    public Long getDiagId() {
        return diagId;
    }

    /**
     * 设置主键ID
     *
     * @param diagId 主键ID
     */
    public void setDiagId(Long diagId) {
        this.diagId = diagId;
    }

    /**
     * 获取诊断名称
     *
     * @return DIAG_NAME - 诊断名称
     */
    public String getDiagName() {
        return diagName;
    }

    /**
     * 设置诊断名称
     *
     * @param diagName 诊断名称
     */
    public void setDiagName(String diagName) {
        this.diagName = diagName == null ? null : diagName.trim();
    }

    /**
     * 获取周期类型 M表示按月 Y表示按天
     *
     * @return PERIOD_TYPE - 周期类型 M表示按月 Y表示按天
     */
    public String getPeriodType() {
        return periodType;
    }

    /**
     * 设置周期类型 M表示按月 Y表示按天
     *
     * @param periodType 周期类型 M表示按月 Y表示按天
     */
    public void setPeriodType(String periodType) {
        this.periodType = periodType == null ? null : periodType.trim();
    }

    /**
     * 获取周期开始时间
     *
     * @return BEGIN_DT - 周期开始时间
     */
    public String getBeginDt() {
        return beginDt;
    }

    /**
     * 设置周期开始时间
     *
     * @param beginDt 周期开始时间
     */
    public void setBeginDt(String beginDt) {
        this.beginDt = beginDt == null ? null : beginDt.trim();
    }

    /**
     * 获取周期结束时间
     *
     * @return END_DT - 周期结束时间
     */
    public String getEndDt() {
        return endDt;
    }

    /**
     * 设置周期结束时间
     *
     * @param endDt 周期结束时间
     */
    public void setEndDt(String endDt) {
        this.endDt = endDt == null ? null : endDt.trim();
    }
}