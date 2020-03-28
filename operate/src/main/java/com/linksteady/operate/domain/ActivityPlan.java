package com.linksteady.operate.domain;

import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author hxcao
 * @date 2019-11-04
 */
@Data
public class ActivityPlan {

    private Long planId;
    
    private Long headId;

    private Long userCnt;

    private LocalDate planDate;

    private Long planDateWid;

    private String planStatus;

    private String activityName;

    private String stage;

    private String planType;

    private int successNum;

    private int version;

     private String effectFlag;

     private Double covRate;
     private Double covAmount;

   public ActivityPlan() {}

    public ActivityPlan(Long headId,LocalDate planDate,String stage,String planType) {
        this.headId = headId;
        this.planDate =planDate;
        this.userCnt=0L;
        this.planStatus = ActivityPlanStatusEnum.NOT_CALCUATE.getStatusCode();
        this.stage = stage;
        this.planDateWid=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(planDate));
        this.planType=planType;
        this.successNum=0;
    }
}
