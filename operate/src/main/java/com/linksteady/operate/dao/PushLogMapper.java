package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.domain.PushLog;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
public interface PushLogMapper {
    /**
     * 写入推送日志
     * @return
     */
    void insertPushLog(PushLog pushLog);

    /**
     * 获取推送日志列表
     * @return
     */
    List<PushLog> getList(int day);

}
