package com.linksteady.operate.service;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.operate.domain.PushListInfo;

import java.util.List;

public interface PushListService {

    /**
     * 获取当前批次待推送的数量
     */
    int getPendingPushCount(int currHour);

    /**
     * 获取当前页待推送列表
     * @return
     */
    List<PushListInfo> getPendingPushList(int limit,int currHour);

    /**
     * 获取分页数据
     * @return
     */
    List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus, String pushDateStr);

    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);
}
