package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-11-04
 */
@Data
public class ActivityPlan {

    private Long headId;

    private Long userCnt;

    private Date planDate;

    private String planDateStr;

    private String planStatus;

    private String activityName;

    private String stage;

    public ActivityPlan() {}

    public ActivityPlan(Long headId, Long userCnt,Date planDate,String planStatus,String stage) {
        this.headId = headId;
        this.userCnt = userCnt;
        this.planDate = planDate;
        this.planStatus = planStatus;
        this.stage = stage;
    }
}
