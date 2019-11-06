package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-10-31
 */
@Data
public class ActivityGroup implements Cloneable{

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
