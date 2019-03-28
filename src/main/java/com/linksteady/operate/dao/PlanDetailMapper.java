package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.PlanDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface PlanDetailMapper extends MyMapper<PlanDetail> {

    List<PlanDetail> getPlanDetail(@Param("year") String year);

    void addPlanDetails(@Param("planDetailList") List<PlanDetail> planDetailList);

    void updateDetail(@Param("planDetail") PlanDetail planDetail);
}