package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface PushListMapper {

    /**
     * 更新推送状态
     */
    void updateSendStatus(@Param("list") List<PushListInfo> list);


    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);

    List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus, String pushDateStr);
}
