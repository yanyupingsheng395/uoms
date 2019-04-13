package com.linksteady.operate.service;

import com.alibaba.fastjson.JSONArray;
import com.linksteady.common.service.IService;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.PlanDetail;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.domain.YearHistory;

import java.util.List;

public interface GmvPlanService extends IService<GmvPlan> {

    List<GmvPlan> getGmvPlanList(int startRow,int endRow);

    int getDataCount();

    List<YearHistory> getYearHistory(String year);

    Double getGmvByYear(String year);

    List<WeightIndex> getWeightIndex(String year);

    List<PlanDetail> getPlanDetail(String year);

    void addPlanAndDetail(String year, String gmv, String rate,String forecastGmv,String forecastRate);

    boolean getPlanAndDetail(String year);

//    void overrideOldData(String year, String gmv, String rate);

    void updateDetail(JSONArray jsonArray);

    int checkYear(String year);

    void deleteDataByYear(String year);

    GmvPlan getByYear(String year);

    void execute(String id);
}
