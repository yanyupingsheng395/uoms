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
    private String followUserName;
    private long msgNum;
    private long executeMsgNum;
    private long coverNum;
    private long executeCoverNum;
    private long convertNum;
    private String convertAmount;
    private String convertRate;
    private long convertSpuNum;
    private String convertSpuAmount;
    private String convertSpuRate;
    private Date statDate;
}
