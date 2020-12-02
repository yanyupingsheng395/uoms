package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-10-24
 */
@Data
public class DailyHead {

    private Long headId;

    private Long totalNum;

    private String descr;

    private Date insertDt;

    private Date updateDt;

    private Date touchDt;

    private String touchDtStr;

    private String status;

    private long successNum;

    /**
     * 转化人数
     */
    private long convertNum;

    /**
     * 转化金额
     */
    private double convertAmount;

    /**
     * 推荐SPU转化人数
     */
    private long convertSpuNum;

    /**
     * 推荐SPU转化金额
     */
    private double convertSpuAmount;

    /**
     * 转化率
     */
    private double convertRate;

    /**
     * 推荐SPU转化率
     */
    private double convertSpuRate;

    /**
     * 最后计算日期
     */
    private Date statDate;

    /**
     * 验证标记
     */
    private String validateLabel;

    /**
     * 效果统计天数
     */
    private long effectDays;

    private int version;
}
