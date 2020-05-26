package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class DailyUserStats {

    /**
     * 用户价值
     */
    private String userValue;
    /**
     * 用户价值名称
     */
    private String userValueLabel;
    /**
     * 活跃度
     */
    private String pathActivity;

    /**
     * 生命周期
     */
    private String lifecycle;

    /**
     * 人数
     */
    private Integer ucnt;

    /**
     * spu名称
     */
    private String spuName;

    /**
     * 产品名称
     */
    private String prodName;

    /**
     * 优惠券门槛
     */
    private Double couponMin;

    /**
     * 优惠券面额
     */
    private Double couponDeno;

    /**
     * 用户成长目标
     */
    private String growthType;

    /**
     * 用户成长目标[序列]
     */
    private String growthSeriesType;
}
