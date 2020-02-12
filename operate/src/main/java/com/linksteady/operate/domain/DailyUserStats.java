package com.linksteady.operate.domain;

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
     * 活跃度名称
     */
    private String getPathActivityLabel;

    /**
     * 生命周期
     */
    private String lifecycle;

    /**
     * 生命周期名称
     */
    private String lifecycleLabel;

    /**
     * 人数
     */
    private Integer ucnt;
    /**
     * 占比
     */
    private double pct;
    /**
     * 格式化后的大小
     */
    private double formatSize;

    /**
     * spu名称
     */
    private String spuName;

    /**
     * 产品名称
     */
    private String prodName;
}
