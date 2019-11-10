package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivitySummaryMapper;
import com.linksteady.operate.domain.ActivitySummary;
import com.linksteady.operate.service.ActivitySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivitySummaryServiceImpl implements ActivitySummaryService {

    @Autowired
    private ActivitySummaryMapper activitySummaryMapper;

    @Override
    public List<ActivitySummary> getUserGroupList(String headId, String planDtWid) {
        return activitySummaryMapper.getUserGroupList(headId, planDtWid);
    }
}
