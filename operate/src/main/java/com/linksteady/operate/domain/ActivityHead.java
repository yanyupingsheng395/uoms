package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.linksteady.common.domain.User;
import lombok.Data;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Data
public class ActivityHead {
    /**
     * 活动ID
     */
    private Long headId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 包含预售
     */
    private String hasPreheat;

    /**
     * 预热状态
     */
    private String preheatStatus;

    /**
     * 预热开始时间
     */
    private String preheatStartDt;

    /**
     * 预热结束时间
     */
    private String preheatEndDt;

    /**
     * 正式状态
     */
    private String formalStatus;

    /**
     * 正式开始时间
     */
    private String formalStartDt;

    /**
     * 正式结束时间
     */
    private String formalEndDt;

    /**
     * 预热数据是否失效
     */
    private String preheatChanged;

    /**
     * 正式数据是否失效
     */
    private String formalChanged;

    /**
     * 预热数据更改时间戳
     */
    private String preheatChangedTime;

    /**
     * 正式数据更改时间戳
     */
    private String formalChangedTime;

    /**
     * 活动类型 S：普通活动，B：大促活动
     */
    private String activityflag;

    /**
     * 预售提醒日期
     */
    private String preheatNotifyDt;

    /**
     * 正式提醒日期
     */
    private String formalNotifyDt;

    private Date insertDt;
    private String insertBy;
    private String preheatNotifyStatus;
    private String formalNotifyStatus;
    private String effectFlag;
    private String activitySource;
    private String platDiscount;
    private String platThreshold;
    private String platDeno;
    private String shopDiscount;
    /**
     * 对应企业成员数量(需要推送消息的成员数)
     */
    private long staffCnt;
    /**
     * 执行推送任务的成员数（人）
     */
    private long executeStaffCnt;
    /**
     * 执行推送成员占比（%）
     */
    private double executeRate;
    /**
     * 推送成功用户数
     */
    private long successNum;
    /**
     * 效果计算天数
     */
    private long effectDays;
    /**
     * 建议推送人数(消息所覆盖的用户数)
     */
    private long totalNum;
    /**
     * 推送成功率
     */
    private double pushSuccessRate;
    /**
     * 全部执行推送预计覆盖用户数
     */
    private long convertNum;
    /**
     * 推送转化率
     */
    private double convertRate;
    /**
     * 推送转化金额
     */
    private double convertAmount;
    /**
     * 推送并购买SPU用户数
     */
    private long convertSpuNum;
    /**
     * 推送并购买SPU转化率
     */
    private double convertSpuRate;
    /**
     * 推送并购买SPU转化金额
     */
    private double convertSpuAmount;
}
