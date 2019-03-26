package com.linksteady.operate.service.impl;

import com.linksteady.common.service.impl.BaseService;
import com.linksteady.operate.dao.GmvPlanMapper;
import com.linksteady.operate.dao.PlanDetailMapper;
import com.linksteady.operate.dao.WeightIndexMapper;
import com.linksteady.operate.dao.YearHistoryMapper;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.PlanDetail;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.domain.YearHistory;
import com.linksteady.operate.service.GmvPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GmvPlanServiceImpl extends BaseService<GmvPlan> implements GmvPlanService {

    @Autowired
    private GmvPlanMapper gmvPlanMapper;

    @Autowired
    private YearHistoryMapper yearHistoryMapper;

    @Autowired
    private WeightIndexMapper weightIndexMapper;

    @Autowired
    private PlanDetailMapper planDetailMapper;

    @Override
    public List<YearHistory> getYearHistory(String year) {
        return yearHistoryMapper.getYearHistory(year);
    }

    @Override
    public List<GmvPlan> getPredictData(String year) {
        return gmvPlanMapper.getPredictData(year);
    }

    @Override
    public List<WeightIndex> getWeightIndex(String year) {
        return weightIndexMapper.getWeightIndex(year);
    }

    @Override
    public List<PlanDetail> getPlanDetail(String year) {
        return planDetailMapper.getPlanDetail(year);
    }
}
