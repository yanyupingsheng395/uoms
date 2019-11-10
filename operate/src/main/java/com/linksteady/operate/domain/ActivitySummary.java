package com.linksteady.operate.domain;

import lombok.Data;

@Data
public class ActivitySummary {

    private Long planDtWid;

    private Long groupId;

    private Long headId;

    private String groupName;

    private String inGrowthPath;

    private String activeLevel;

    private Long groupUserCnt;

    private Long growthUserCnt;

    private Long activeUserCnt;

    private String activityStage;

    private String smsTemplateCode;

    private String smsTemplateContent;
}
