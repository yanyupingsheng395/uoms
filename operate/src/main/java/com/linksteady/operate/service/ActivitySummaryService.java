package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivitySummary;

import java.util.List;

public interface ActivitySummaryService {

    List<ActivitySummary> getUserGroupList(String headId, String planDtWid);

    /**
     * 更新统计表信息
     * @param headId
     * @param hasPreheat
     */
    void saveSummaryList(String headId, String hasPreheat);
}
