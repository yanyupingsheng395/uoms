package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

/**
 * 计划头表
 * @author  genereate
 */
@Table(name = "UO_GMV_PLAN")
@Data
public class GmvPlan {
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
     * gmv目标值
     */
    @Column(name = "GMV_TARGET")
    private Double gmvTarget;

    /**
     * 目标值相比上年增长率
     */
    @Column(name = "TARGET_RATE")
    private Double targetRate;

    /**
     * 状态  D表示草稿  C表示更新数据中 E表示已下达执行
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 预测GMV值
     */
    @Column(name = "FORECAST_GMV")
    private Double forecastGmv;

    /**
     * 预测GMV值相比上年增长率
     */
    @Column(name = "FORECAST_RATE")
    private Double forecastRate;

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


}