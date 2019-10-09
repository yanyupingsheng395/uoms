package com.linksteady.operate.push;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.operate.domain.PushListInfo;

import java.util.List;

public interface PushListService {

    /**
     * 获取待推送的最大pushId
     */
    int getPendingPushMaxId(int currHour);


    /**
     * 获取当前批次待推送的数量
     */
    int getPendingPushCount(int maxPushId,int currHour);

    /**
     * 获取当前页待推送列表
     * @return
     */
    List<PushListInfo> getPendingPushList(int maxPushId, int start, int end,int currHour);

    /**
     * 更新当前批次的IS_PUSH字段
     */
    void updateIsPush(int maxPushId,int currHour);

    /**
     * 获取分页数据
     * @return
     */
    List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus);

    int getTotalCount(String sourceCode, String pushStatus);
}
