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
     * 推送启动或停止  value:start,stop
     */
    private String startOrStop;
}
