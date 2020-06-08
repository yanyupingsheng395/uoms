package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface PushListMapper {

    /**
     * 更新推送状态
     */
    void updateSendStatus(@Param("list") List<PushListInfo> list);


    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);

    List<PushListInfo> getPushInfoListPage(int limit, int offset, String sourceCode, String pushStatus, String pushDateStr);

    void insertTestMsg(PushListInfo pushListInfo);

    Map<String, Object> getPushDataOfDay(int day);

    Map<String, Object> getPushDataOfMonth(String startDt, String endDt);

    Map<String, Object> getRptAndBlackData(int day, String startDt, String endDt);
}
