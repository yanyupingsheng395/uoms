package com.linksteady.operate.service;

import com.linksteady.operate.domain.PushListInfo;

import java.util.List;
import java.util.Map;

public interface PushListService {

    /**
     * 获取分页数据
     * @return
     */
    List<PushListInfo> getPushInfoListPage(int limit, int offset, String sourceCode, String pushStatus, String pushDateStr);

    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);

    Map<String, Object> getPushData(int day);

    Map<String, Object> getRptAndBlackData(int day);
}
