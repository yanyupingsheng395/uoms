package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivitySummary;

import java.util.List;

public interface ActivitySummaryMapper {

    List<ActivitySummary> getUserGroupList(String headId, String planDtWid);

    void saveActivitySummaryList(List<ActivitySummary> dataList);
}
