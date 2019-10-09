package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface PushListMapper {

    /**
     * 获取待推送的最大pushId
     * @return
     */
    int getPendingPushMaxId(int currHour);

    /**
     * 获取当前批次待推送的数量
     */
    int getPendingPushCount(int maxPushId,int currHour);


    /**
     * 获得当前要推送的列表(分页)
     * @return
     */
    List<PushListInfo> getPendingPushList(@Param("maxPushId") int maxPushId, @Param("start") int start, @Param("end") int end,@Param("currHour") int currHour);

    /**
     * 更新推送状态
     */
    void updateSendStatus(@Param("list") List<PushListInfo> list);

    /**
     * 更新是否推送字段
     */
    void updateIsPush(int maxPushId,int currHour);

    int getTotalCount(String sourceCode, String pushStatus);

    List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus);
}
