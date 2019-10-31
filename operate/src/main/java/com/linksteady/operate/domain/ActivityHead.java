package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

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
     * 活动阶段 预热，正式
     */
    private String activityStage;

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
}
