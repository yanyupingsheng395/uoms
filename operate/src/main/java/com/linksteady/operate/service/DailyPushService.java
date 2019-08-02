package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyPush;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-02
 */

public interface DailyPushService {

    List<DailyPush> getPageList(int start, int end, String headId);

    int getDataTotalCount(String headId);
}
