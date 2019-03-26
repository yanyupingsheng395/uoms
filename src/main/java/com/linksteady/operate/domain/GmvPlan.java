package com.linksteady.operate.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "UO_GMV_PLAN")
public class GmvPlan {
    /**
     * 计划ID
     */
    @Column(name = "PLAN_ID")
    private long planId;

    /**
     * 年ID
     */
    @Column(name = "YEAR_ID")
    private long yearId;

    /**
     * gmv目标值
     */
    @Column(name = "GMV_TARGET")
    private long gmvTarget;

    /**
     * 目标值相比上年增长率
     */
    @Column(name = "TARGET_RATE")
    private long targetRate;

    /**
     * 状态  D表示草稿  C表示更新数据中 E表示已下达执行
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 预测GMV值
     */
    @Column(name = "FORECAST_GMV")
    private long forecastGmv;

    /**
     * 预测GMV值相比上年增长率
     */
    @Column(name = "FORECAST_RATE")
    private long forecastRate;

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

    /**
     * 更新人
     */
    @Column(name = "UPDATE_BY")
    private String updateBy;

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public long getYearId() {
        return yearId;
    }

    public void setYearId(long yearId) {
        this.yearId = yearId;
    }

    public long getGmvTarget() {
        return gmvTarget;
    }

    public void setGmvTarget(long gmvTarget) {
        this.gmvTarget = gmvTarget;
    }

    public long getTargetRate() {
        return targetRate;
    }

    public void setTargetRate(long targetRate) {
        this.targetRate = targetRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getForecastGmv() {
        return forecastGmv;
    }

    public void setForecastGmv(long forecastGmv) {
        this.forecastGmv = forecastGmv;
    }

    public long getForecastRate() {
        return forecastRate;
    }

    public void setForecastRate(long forecastRate) {
        this.forecastRate = forecastRate;
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
}