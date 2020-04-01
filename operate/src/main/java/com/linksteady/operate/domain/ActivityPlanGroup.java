package com.linksteady.operate.domain;

import lombok.Data;

@Data
public class ActivityPlanGroup {

    private Long groupId;

    private Long headId;

    private Long planId;

    private String groupName;

    private String prodActivityProp;

    private String checkAllow;

    private String checkFlag;

    private Long groupUserNum;

    private String checkAllowDesc;

}


