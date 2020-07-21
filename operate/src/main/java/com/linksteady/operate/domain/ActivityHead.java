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
}
