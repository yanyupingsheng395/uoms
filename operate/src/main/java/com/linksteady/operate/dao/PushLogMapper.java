package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushLog;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
public interface PushLogMapper {

    /**
     * 获取推送日志列表
     * @return
     */
    List<PushLog> getList(int day);

}
