package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huang
 * @date 2019-08-13
 */
@Data
public class ActivityPlanEffect implements Serializable {
    private String planId;

    private long userCount;
    private long successCount;
    //推送成本
    private Double pushCost;
    //推送转化人数
    private long covUserCount;
    //推送转化金额
    private Double covAmount;
    //推送转化率
    private long covRate;
    //每推送成本带来收入
    private Double pushPerIncome;

    //在推荐类目转化人数
    private long spuUserCount;

    //在类目转化金额
    private Double spuAmount;

    //转化天数
    private String conversionDate;
}
