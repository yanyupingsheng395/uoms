package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class ActivityPlanEffectVO {

    private long userCount;

    private long successCount;
    //推送成本
    private Double pushCost;

    //转化人数
    private long covUserCount;
    //推送转化金额
    private Double covAmount;
    //推送转化率
    private Double covRate;

    //每推送成本带来收入
    private Double pushPerIncome;

}
