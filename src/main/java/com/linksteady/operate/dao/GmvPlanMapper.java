package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.PlanDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface GmvPlanMapper extends MyMapper<GmvPlan> {
    List<GmvPlan> getPredictData(@Param("year") String year);
    Long getPlanId();
}