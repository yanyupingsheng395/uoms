package com.linksteady.operate.domain;

import com.linksteady.operate.domain.enums.PushSignalEnum;
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
     * singal 信号  start表示启动推送服务 stop表示停止推送服务 refresh表示刷新配置 print 打印配置
     */
    private PushSignalEnum signal;

    /**
     * 获取状态报告 最后响应时间
     */
    private LocalDateTime lastRptDate;

    /**
     * 获取上行消息最后响应时间
     */
    private LocalDateTime lastMoDate;


}
