package com.linksteady.operate.domain;

import lombok.Data;

@Data
public class ActivitySummary implements Cloneable{

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


    public ActivitySummary() {}

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ActivitySummary(Long groupId, Long headId, String groupName, String inGrowthPath, String activeLevel, Long planDtWid) {
        this.groupId = groupId;
        this.headId = headId;
        this.groupName = groupName;
        this.inGrowthPath = inGrowthPath;
        this.activeLevel = activeLevel;
        this.planDtWid = planDtWid;
    }
}
