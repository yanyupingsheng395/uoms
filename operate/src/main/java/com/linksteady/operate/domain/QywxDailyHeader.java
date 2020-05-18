package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-10-24
 */
@Data
public class QywxDailyHeader {

    private Long headId;

    private Long totalNum;

    private Date taskDate;

    private String taskDateStr;

    private String status;

    private Long successNum;

    /**
     * 转化人数
     */
    private Long convertNum;

    /**
     * 转化金额
     */
    private Double convertAmount;

    /**
     * 转化率
     */
    private Double convertRate;


    /**
     * 推荐SPU转化人数
     */
    private Long convertSpuNum;

    /**
     * 推荐SPU转化金额
     */
    private Double convertSpuAmount;

    /**
     * 推荐SPU转化率
     */
    private Double convertSpuRate;

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
    private Long effectDays;

    private int version;

    private Date insertDt;

    private Date updateDt;
}
