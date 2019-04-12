package com.linksteady.operate.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "UO_GMV_PLAN_DETAIL")
public class PlanDetail {
    /**
     * 计划明细ID
     */
    @Column(name = "PLAN_DETAIL_ID")
    private Long planDetailId;

    /**
     * 计划ID
     */
    @Column(name = "PLAN_ID")
    private Long planId;

    /**
     * 年ID
     */
    @Column(name = "YEAR_ID")
    private Long yearId;

    /**
     * 月份ID
     */
    @Column(name = "MONTH_ID")
    private Long monthId;

    /**
     * gmv目标值
     */
    @Column(name = "GMV_VALUE")
    private Double gmvValue;

    /**
     * gmv目标占全年比例
     */
    @Column(name = "GMV_PCT")
    private Double gmvPct;

    /**
     * 去年同比gmv值
     */
    @Column(name = "GMV_TB")
    private Double gmvTb;

    /**
     * 同比去年增长率
     */
    @Column(name = "GMV_TB_RATE")
    private Double gmvTbRate;

    /**
     * 创建日期
     */
    @Column(name = "CREATE_DT")
    private Date createDt;

    /**
     * 更新日期
     */
    @Column(name = "UPDATE_DT")
    private Date updateDt;

    /**
     * 创建人
     */
    @Column(name = "CREATE_BY")
    private String createBy;

    @Column(name = "isHistory")
    private String isHistory;

    public Long getPlanDetailId() {
        return planDetailId;
    }

    public void setPlanDetailId(Long planDetailId) {
        this.planDetailId = planDetailId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getYearId() {
        return yearId;
    }

    public void setYearId(Long yearId) {
        this.yearId = yearId;
    }

    public Long getMonthId() {
        return monthId;
    }

    public void setMonthId(Long monthId) {
        this.monthId = monthId;
    }

    public Double getGmvValue() {
        return gmvValue;
    }

    public void setGmvValue(Double gmvValue) {
        this.gmvValue = gmvValue;
    }

    public Double getGmvPct() {
        return gmvPct;
    }

    public void setGmvPct(Double gmvPct) {
        this.gmvPct = gmvPct;
    }

    public Double getGmvTb() {
        return gmvTb;
    }

    public void setGmvTb(Double gmvTb) {
        this.gmvTb = gmvTb;
    }

    public Double getGmvTbRate() {
        return gmvTbRate;
    }

    public void setGmvTbRate(Double gmvTbRate) {
        this.gmvTbRate = gmvTbRate;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 更新人
     */
    @Column(name = "UPDATE_BY")
    private String updateBy;

    public String getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(String isHistory) {
        this.isHistory = isHistory;
    }
}