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
     * 活动类型
     */
    private String activityType;

    /**
     * 活动开始时间
     */
    private String startDate;

    /**
     * 活动结束时间
     */
    private String endDate;

    /**
     * 预计覆盖人数
     */
    private Long coverNum;

    /**
     * 活动转化人数
     */
    private Long convertNum;

    /**
     * （转化人数）年同比
     */
    private Double numYearPercent;

    /**
     * 转化金额
     */
    private Double convertAmount;

    /**
     * （转化金额）年同比
     */
    private Double amountYearPercent;

    /**
     * 状态
     */
    private String status;

    /**
     * 活动影响开始日期
     */
    private String beforeDate;

    /**
     * 活动影响结束日期
     */
    private String afterDate;

    /**
     * 创建日期
     */
    private String createDt;

}
