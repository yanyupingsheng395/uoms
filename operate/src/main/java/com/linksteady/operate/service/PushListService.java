package com.linksteady.operate.service;

import com.linksteady.operate.domain.PushListInfo;

import java.util.List;

public interface PushListService {

    /**
     * 获取分页数据
     * @return
     */
    List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus, String pushDateStr);

    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);
}
