package com.linksteady.qywx.domain;

import lombok.Data;

@Data
public class PhoneFixStatis {

    private String followerUserId;

    private int userCnt;

    private int blankCnt;

    private int invalidCnt;
}
