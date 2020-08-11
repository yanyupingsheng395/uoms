package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AddUserSchedule {

    private long scheduleId;
    private Long headId;
    private long applyNum;
    private double applyRate;
    private long waitAddNum;
    private long remainUserCnt;
    private long waitDays;

    private long remainAddNum;
    private Long contactwayId;
    private String state;
    private String contactwayUrl;
    private String smsContent;
    private String status;
    private Date insertDt;
    private String insertBy;
    private Date updateDt;
    private String updateBy;
    private long scheduleDateWid;
    private Date scheduleDate;
    private long applyPassNum;

}
