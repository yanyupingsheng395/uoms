package com.linksteady.operate.thread;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MonitorThread {

    private ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(3,new BasicThreadFactory.Builder().namingPattern("monitor-schedule-pool-%d").build());


    private LocalDateTime lastPushDate;

    private LocalDateTime lastBatchPushDate;

    private LocalDateTime lastPurgeDate;

    private static MonitorThread instance = new MonitorThread();

    public static MonitorThread getInstance() {
        return instance;
    }

    public LocalDateTime getLastPushDate() {
        return getInstance().lastPushDate;
    }

    public void setLastPushDate(LocalDateTime lastPushDate) {
        getInstance().lastPushDate=lastPushDate;
    }

    public LocalDateTime getLastBatchPushDate() {
        return getInstance().lastBatchPushDate;
    }

    public void setLastBatchPushDate(LocalDateTime lastBatchPushDate) {
       getInstance().lastBatchPushDate=lastBatchPushDate;
    }

    public LocalDateTime getLastPurgeDate() {
        return getInstance().lastPurgeDate;
    }

    public void setLastPurgeDate(LocalDateTime lastPurgeDate) {
        getInstance().lastPurgeDate = lastPurgeDate;
    }

    /**
     * 进行预警
     */
    public void start() {
        //TODO scheduledExecutorService 启动线程对要监控的线程的最后响应时间进行判断，超过2小时不响应，则进行预警
    }
}
