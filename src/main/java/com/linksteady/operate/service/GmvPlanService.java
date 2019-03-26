package com.linksteady.operate.service;

import com.linksteady.common.service.IService;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.PlanDetail;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.domain.YearHistory;

import java.util.List;

public interface GmvPlanService extends IService<GmvPlan> {

    List<YearHistory> getYearHistory(String year);

    List<GmvPlan> getPredictData(String year);

    List<WeightIndex> getWeightIndex(String year);

    List<PlanDetail> getPlanDetail(String year);
}
