package com.linksteady.operate.service;

import com.linksteady.operate.domain.PushLog;

import java.util.List;

public interface PushLogService {

    void saveMessage(PushLog pushLog);

    void consumeLog();

    /**
     * 获取最近的日志
     */
    List<PushLog> getPushLogList(int day);

}
