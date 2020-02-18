package com.linksteady.operate.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推送服务的心跳接收对象
 */
@Data
public class HeartBeatInfo implements Serializable {

       /**
     * 每日运营最后一次响应时间
     */
    private LocalDateTime lastPushDate;

    /**
     * 批量推送最后一次响应时间
     */
    private LocalDateTime lastBatchPushDate;

    /**
     * 骚扰拦截最后一次响应时间
     */
    private LocalDateTime lastPurgeDate;

    /**
     * start表示启动推送服务 stop表示停止推送服务 refresh表示刷新推送服务
     */
    private String startOrStop;

    /**
     * 获取状态报告 最后响应时间
     */
    private LocalDateTime lastRptDate;

    /**
     * 获取上行消息最后响应时间
     */
    private LocalDateTime lastMoDate;


}
