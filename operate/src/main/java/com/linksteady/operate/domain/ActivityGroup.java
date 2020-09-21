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

    private String groupInfo;

    private String activityStage;

    private Long smsTemplateCode;

    private String activityType;

    /**
     * 活动推送内容
     */
    private String smsTemplateContent;

    private String insertBy;

    private Date insertDt;

    private String checkFlag;

    private String checkComments;

    public ActivityGroup() {};

    public ActivityGroup(Long groupId, Long headId, String activityType, String groupName, String insertBy, Date insertDt, String groupInfo) {
        this.groupId = groupId;
        this.headId = headId;
        this.activityType = activityType;
        this.groupName = groupName;
        this.insertBy = insertBy;
        this.insertDt = insertDt;
        this.groupInfo = groupInfo;
    }

    public ActivityGroup(Long groupId, Long headId, String groupName, String activityStage, String activityType,
                         String insertBy, Date insertDt, String groupInfo) {
        this.groupId = groupId;
        this.headId = headId;
        this.groupName = groupName;
        this.activityStage = activityStage;
        this.activityType = activityType;
        this.insertBy = insertBy;
        this.insertDt = insertDt;
        this.groupInfo = groupInfo;
    }
}
