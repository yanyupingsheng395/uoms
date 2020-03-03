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
     * 推荐SPU转化人数
     */
    private Long convertSpuNum;

    /**
     * 推荐SPU转化金额
     */
    private Double convertSpuAmount;

    /**
     * 转化率
     */
    private Double convertRate;

    /**
     * 推荐SPU转化率
     */
    private Double convertSpuRate;

    /**
     * 最后计算日期
     */
    private Date statDate;

    /**
     * 操作的时间戳
     */
    private Long opChangeDate;

    /**
     * 验证标记
     */
    private String validateLabel;

    /**
     * 效果统计天数
     */
    private Long effectDays;
}
