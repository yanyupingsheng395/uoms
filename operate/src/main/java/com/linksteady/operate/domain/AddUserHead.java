package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@Data
public class AddUserHead {

    private long id;
    private Long applyUserCnt;
    private Long applyPassCnt;
    private String applyPassRate;
    private Date taskStartDt;
    private String taskStatus;
    private Date insertDt;
    private String insertBy;
    private Date updateDt;
    private String updateBy;
    private Date taskEndDt;
    private String taskName;
    private long deliveredUserCnt;
    private long waitUserCnt;
    private long dailyUserCnt;
    private double dailyApplyRate;
    private long dailyAddUserCnt;
    private long dailyWaitDays;
    private long dailyAddTotal;
    private Long contactWayId;
    private String contactWayUrl;
    private String smsContent;
    private String sourceId;
    private String sourceName;
    private String regionId;
    private String regionName;
    private String sendType;
    private int version;
    private String isSourceName;
    private String isProdName;

    private long actualApplyNum;
}
