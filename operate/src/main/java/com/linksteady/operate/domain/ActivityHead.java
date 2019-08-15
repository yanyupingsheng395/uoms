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
    private String actName;

    /**
     * 活动类型
     */
    private String actType;

    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    private Date beginDt;

    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    private Date endDt;

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
     * 活动延展天数
     */
    private Long dateRange;

}
