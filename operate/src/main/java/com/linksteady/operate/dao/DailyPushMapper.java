package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface DailyPushMapper {

    List<DailyPushQuery> getDataList(@Param("headId") String headId);

    void savePushInfo(List<DailyPushInfo> list);

    List<DailyPushInfo> getSendSmsList();

    void updateSendStatus(@Param("list") List<DailyPushInfo> list, @Param("status") String status);

    void updateHeaderToDone();
}
