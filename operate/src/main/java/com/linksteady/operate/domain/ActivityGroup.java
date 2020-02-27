package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-10-31
 */
@Data
public class ActivityGroup implements Cloneable{

    private Long groupId;

    private Long headId;

    private String groupName;

    private String activityStage;

    private Long userNum;

    private String smsTemplateCode;

    private String activityType;

    /**
     * 活动推送内容
     */
    private String smsTemplateContent;

    private String insertBy;

    private Date insertDt;

    public ActivityGroup() {};

    public ActivityGroup(Long groupId, Long headId, String groupName, String activityStage, String activityType, String insertBy, Date insertDt) {
        this.groupId = groupId;
        this.headId = headId;
        this.groupName = groupName;
        this.activityStage = activityStage;
        this.activityType = activityType;
        this.insertBy = insertBy;
        this.insertDt = insertDt;
    }
}
