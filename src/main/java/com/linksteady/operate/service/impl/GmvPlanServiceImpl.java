package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
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
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

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
    public List<GmvPlan> getGmvPlanList(int startRow, int endRow) {
        return gmvPlanMapper.getGmvPlanList(startRow, endRow);
    }

    @Override
    public int getDataCount() {
        return gmvPlanMapper.getDataCount();
    }

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

    @Override
    @Transactional
    public void addPlanAndDetail(String year, String gmv, String rate) {
        Long planId = gmvPlanMapper.getPlanId();
        addPlan(year, gmv, rate, planId);
        addPlanDetail(year, gmv, rate, planId);
    }

    @Override
    public boolean getPlanAndDetail(String year) {
        Example plan = new Example(GmvPlan.class);
        plan.createCriteria().andCondition("year_id=", year);
        List<GmvPlan> gmvPlanList= gmvPlanMapper.selectByExample(plan);
        Example detail = new Example(PlanDetail.class);
        detail.createCriteria().andCondition("year_id=", year);
        List<PlanDetail> planDetailList = planDetailMapper.selectByExample(detail);

        if(gmvPlanList.size() > 0 && planDetailList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void overrideOldData(String year, String gmv, String rate) {
        Example plan = new Example(GmvPlan.class);
        plan.createCriteria().andCondition("year_id=", year);
        gmvPlanMapper.deleteByExample(plan);

        Example detail = new Example(PlanDetail.class);
        plan.createCriteria().andCondition("year_id=", year);
        planDetailMapper.deleteByExample(detail);
        // 新增数据
        addPlanAndDetail(year, gmv, rate);
    }

    @Override
    public void updateDetail(JSONArray jsonArray) {
        // todo
        List<PlanDetail> list = new ArrayList<>();
        for(int i=0; i<jsonArray.size(); i++) {
            PlanDetail planDetail = new PlanDetail();
            planDetail.setMonthId(Long.valueOf(i + 1));
            planDetail.setGmvValue(jsonArray.getDoubleValue(i));
            list.add(planDetail);
        }

        for (PlanDetail p:list) {
            planDetailMapper.updateDetail(p);
        }
    }

    @Override
    public int checkYear(String year) {
        return gmvPlanMapper.findByYear(year);
    }

    @Override
    @Transactional
    public void deleteDataByYear(String year) {
        gmvPlanMapper.deleteDataByYear(year);
        planDetailMapper.deleteDataByYear(year);
    }

    @Override
    public GmvPlan getByYear(String year) {
        return gmvPlanMapper.getByYear(year);
    }

    private void addPlanDetail(String year, String gmv, String rate, Long planId) {
        List<PlanDetail> planDetailList = new ArrayList<>();
        for(int i=1; i<=12; i++) {
            double gmvValue = Double.valueOf(String.format("%.2f", Double.valueOf(gmv)/12));
            double tbRate = Double.valueOf(String.format("%.2f", 1/12d));
            PlanDetail planDetail = new PlanDetail();
            planDetail.setGmvValue(gmvValue);
            planDetail.setYearId(Long.valueOf(year));
            planDetail.setMonthId(Long.valueOf(i));
            planDetail.setGmvTbRate(Double.valueOf(rate));
            planDetail.setGmvPct(tbRate + 0.2);
            planDetail.setPlanId(planId);
            planDetail.setGmvTb(2852.55);
            planDetailList.add(planDetail);
            if("2019".equals(year)) {
                if(i <= 4) {
                    planDetail.setIsHistory("Y");
                }else {
                    planDetail.setIsHistory("N");
                }
            }else {
                planDetail.setIsHistory("N");
            }
        }
        planDetailMapper.addPlanDetails(planDetailList);
    }

    @Override
    public void execute(String id) {
        gmvPlanMapper.updateStatus(id);
    }

    private void addPlan(String year, String gmv, String rate, Long planId) {
        GmvPlan gmvPlan = new GmvPlan();
        gmvPlan.setYearId(Long.valueOf(year));
        gmvPlan.setGmvTarget(Double.valueOf(gmv));
        gmvPlan.setTargetRate(Double.valueOf(rate));
        gmvPlan.setPlanId(planId);
        gmvPlan.setForecastGmv(2000);
        gmvPlan.setForecastRate(0.25);
        gmvPlan.setStatus("D");
        gmvPlanMapper.insert(gmvPlan);
    }
}
