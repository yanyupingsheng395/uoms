package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AddUser {
    private Long id;
    private Long headId;
    private Long userId;
    private String phoneNum;
    private String isPush;
    private String pushStatus;
    private Date insertDt;
    private Date updateDt;
    private String smsContent;
    private Long scheduleId;
    private String state;



}
