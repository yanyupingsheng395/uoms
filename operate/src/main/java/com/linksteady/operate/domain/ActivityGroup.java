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

    private Long userNum;

    private String smsTemplateCode;

    private String activityType;

    /**
     * 活动推送内容
     */
    private String smsTemplateContent;

    private String insertBy;

    private Date insertDt;

    private String checkFlag;

    private String checkComments;

    /**
     * 活动属性 Y：是，N：否
     */
    private String prodActivityProp;

    public ActivityGroup() {};

    public ActivityGroup(Long groupId, Long headId, String groupName, String activityStage, String activityType,
                         String insertBy, Date insertDt, String prodActivityProp, String groupInfo) {
        this.groupId = groupId;
        this.headId = headId;
        this.groupName = groupName;
        this.activityStage = activityStage;
        this.activityType = activityType;
        this.insertBy = insertBy;
        this.insertDt = insertDt;
        this.prodActivityProp = prodActivityProp;
        this.groupInfo = groupInfo;
    }
}
