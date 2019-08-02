package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyPush;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-02
 */
public interface DailyPushMapper {
    List<DailyPush> getPushList(int start, int end, String headId);

    int getDataTotalCount(String headId);
}
