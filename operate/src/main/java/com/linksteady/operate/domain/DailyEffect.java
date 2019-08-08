package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-08-01
 */
@Data
public class DailyEffect {

    private Long headId;

    /**
     * 触达率
     */
    private Double touchRate;

    /**
     * 损耗率
     */
    private Double lossRate;

    /**
     * 放弃率
     */
    private Double abandonRate;

    /**
     * 转化率
     */
    private Double convertRate;

    /**
     * 转化金额
     */
    private Double convertAmount;

    /**
     * 补贴金额
     */
    private Double subsidyAmount;

    /**
     * 补贴数量
     */
    private Long subsidyCount;

    /**
     * 核销率
     */
    private Double cancleRate;

    /**
     * 核销数量
     */
    private Long cancleCount;

    /**
     * 补贴ROI
     */
    private Double subsidyRoi;

    /**
     * 转化人数
     */
    private Long convertCount;

    /**
     * 触达日期
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    private Date touchDt;

    /**
     * 建议推送人数
     */
    private Long totalNum;

    /**
     * 实际推送人数
     */
    private Long actualNum;

    /**
     * 触达人数
     */
    private Long touchCount;

    /**
     * 每元补贴创收
     */
    private Double subsidyPerUnit;
}
