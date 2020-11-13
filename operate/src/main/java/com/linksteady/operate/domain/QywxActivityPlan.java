package com.linksteady.operate.domain;

import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class QywxActivityPlan {

    /**
     * 头表ID
     */
    private Long headId;
    /**
     * 用户数
     */
    private Long userCnt;
    /**
     * 计划日期
     */
    private LocalDate planDate;
    /**
     * 计划状态  尚未计算0   待执行1   执行中2   执行完3  已过期4  已终止5
     */
    private String planStatus;
    /**
     * 插入时间
     */
    private LocalDate insertDt;
    /**
     * 最后计算时间
     */
    private LocalDate calculateDt;
    /**
     * 计划日期 PLAN_DATE的NUMBER类型
     */
    private Long planDateWid;
    /**
     * 执行开始计划的人
     */
    private String startBy;
    /**
     * 执行开始计划的时间
     */
    private LocalDate startDt;
    /**
     * 执行停止计划的人
     */
    private String stopBy;
    /**
     * 执行停止计划的时间
     */
    private LocalDate stopDt;
    /**
     * 计划类型	NOTIFY 表示通知  DURING表示期间
     */
    private String planType;
    /**
     * 成功推送人数
     */
    private Long successNum;
    /**
     * 失败人数
     */
    private Long faildNum;
    /**
     * 版本号 乐观锁解决并发 默认为0
     */
    private int version;
    /**
     *推送方式
     */
    private String pushMethod;
    /**
     *推送时段
     */
    private String pushPeriod;
    /**
     * 计划ID
     */
    private Long planId;
    /**
     * 效果是否计算 Y表示是 N表示否
     */
    private String effectFlag;
    /**
     * 拦截人数
     */
    private Long interceptNum;
    /**
     * 转化率
     */
    private Double covRate;
    /**
     * 转化金额
     */
    private Double covAmount;
    /**
     * 备注
     */
    private String remark;

   public QywxActivityPlan() {}

    public QywxActivityPlan(Long headId, LocalDate planDate, String stage, String planType) {
        this.headId = headId;
        this.planDate =planDate;
        this.userCnt=0L;
        this.planStatus = ActivityPlanStatusEnum.NOT_CALCUATE.getStatusCode();
        this.planDateWid=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(planDate));
        this.planType=planType;
    }

    /**
     * 为了应对企业微信，增加的一个构造方法
     * @param headId
     * @param planDate
     * @param planType
     */
    public QywxActivityPlan(Long headId, LocalDate planDate, String planType) {
        this.headId = headId;
        this.planDate =planDate;
        this.userCnt=0L;
        this.planStatus = ActivityPlanStatusEnum.NOT_CALCUATE.getStatusCode();
        this.planDateWid=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(planDate));
        this.planType=planType;
    }
}
