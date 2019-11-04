package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface PushListMapper {

    /**
     * 获取当前批次待推送的数量
     */
    int getPendingPushCount(int currHour);


    /**
     * 获得当前要推送的列表(分页)
     * @param limit 要获取数据的条数
     * @param currHour 时段
     * @return
     */
    List<PushListInfo> getPendingPushList(@Param("limit") int limit,@Param("currHour") int currHour);

    /**
     * 更新推送状态
     */
    void updateSendStatus(@Param("list") List<PushListInfo> list);


    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);

    List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus, String pushDateStr);
}
