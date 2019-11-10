package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivitySummary;

import java.util.List;

public interface ActivitySummaryService {

    List<ActivitySummary> getUserGroupList(String headId, String planDtWid);
}
