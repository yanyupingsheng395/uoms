package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class DailyInfo {

//    HEAD_ID            NUMBER,
//    TOTAL_NUM          NUMBER,
//    DESCR              VARCHAR2(512),
//    INSERT_DT          DATE,
//    UPDATE_DT          DATE,
//    TOUCH_DT           DATE,
//    STATUS             VARCHAR2(16),
//    SUCCESS_NUM        NUMBER default 0,
//    CONVERT_NUM        NUMBER,
//    CONVERT_AMOUNT     NUMBER,
//    CONVERT_SPU_NUM    NUMBER,
//    CONVERT_SPU_AMOUNT NUMBER,
//    CONVERT_RATE       NUMBER,
//    CONVERT_SPU_RATE   NUMBER,
//    STAT_DATE          DATE

    /**
     * 头ID
     */
    private Long headId;

    /**
     * 任务建议人数
     */
    private Long totalNum;

    /**
     * 实际推送人数
     */
    private Long actualNum;

    /**
     * 触达率
     */
    private Double touchRate;

    /**
     * 转化人数
     */
    private Long convertCount;

    /**
     * 转化率
     */
    private Double convertRate;

    /**
     * 转化金额
     */
    private Double convertAmount;

    /**
     * 触达日期
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    private Date touchDt;

    /**
     * 状态
     */
    private String status;

    /**
     * 计划人数
     */
    private Long planNum;

    /**
     * 失败人数
     */
    private Long faildNum;

    /**
     * 成功人数
     */
    private Long successNum;

    /**
     * 触达人数
     */
    private Long touchCount;

    /**
     * 执行率
     */
    private Double executeRate;
}
