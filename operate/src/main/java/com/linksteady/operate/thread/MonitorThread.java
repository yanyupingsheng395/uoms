package com.linksteady.operate.thread;

import java.time.LocalDateTime;

public class MonitorThread {

    private LocalDateTime lastPushDate;

    private LocalDateTime lastBatchPushDate;

    private LocalDateTime lastPurgeDate;

    private LocalDateTime lastRptDate;

    private LocalDateTime lastMoDate;

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

    public LocalDateTime getLastRptDate() {
        return lastRptDate;
    }

    public void setLastRptDate(LocalDateTime lastRptDate) {
        this.lastRptDate = lastRptDate;
    }

    public LocalDateTime getLastMoDate() {
        return lastMoDate;
    }

    public void setLastMoDate(LocalDateTime lastMoDate) {
        this.lastMoDate = lastMoDate;
    }

}
