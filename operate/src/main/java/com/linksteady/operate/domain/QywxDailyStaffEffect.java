package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/9/14
 */
@Data
public class QywxDailyStaffEffect {

    private long headId;
    private String followUserId;
    private long msgNum;
    private long executeMsgNum;
    private long coverNum;
    private long executeCoverNum;
    private long convertNum;
    private long convertAmount;
    private double convertRate;
    private long convertSpuNum;
    private long convertSpuAmount;
    private double convertSpuRate;
    private Date statDate;
}
