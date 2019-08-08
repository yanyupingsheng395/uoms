package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-08-06
 */
@Data
public class DailyExecute {

    private Long headId;

    /**
     * 转化人数
     */
    private Long convertCnt;

    /**
     * 补贴核销数
     */
    private Long sellCnt;

    /**
     * 转化金额
     */
    private Double convertAmount;

    /**
     * 任务执行时间
     */
    private String executeDt;
}
