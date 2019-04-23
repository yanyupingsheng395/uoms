package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.common.util.ArithUtil;
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

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
    public List<GmvPlan> getGmvPlanList(int startRow, int endRow) {
        return gmvPlanMapper.getGmvPlanList(startRow, endRow);
    }

    @Override
    public int getDataCount() {
        return gmvPlanMapper.getDataCount();
    }

    @Override
    public Double getGmvByYear(String year) {
        return yearHistoryMapper.getGmvByYear(year);
    }

    @Override
    public List<YearHistory> getYearHistory(String year) {
        return yearHistoryMapper.getYearHistory(year);
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
    public void addPlanAndDetail(String year, String gmv, String rate,String forecastGmv,String forecastRate) {
        Long planId = gmvPlanMapper.getPlanId();
        addPlan(year, gmv, rate, planId,forecastGmv,forecastRate);
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

    @SuppressWarnings("AlibabaMethodTooLong")
    private void addPlanDetail(String year, String gmv, String rate, Long planId) {
        List<PlanDetail> planDetailList = new ArrayList<>();

        List<Long>  monthList=new ArrayList(){{
            for(int i=1;i<=12;i++)
            {
                this.add(Long.parseLong(year+ String.format("%02d", i)));
            }

        }};
        //gmv按权重指数进行拆解
        //获取上一年的权重指数
        List<WeightIndex> lastYearIndex=weightIndexMapper.getWeightIndex(String.valueOf(Integer.parseInt(year)-1));

        //权重指数和
        Double indexTotal=0d;
        Map<Long,Double> pctMap= Maps.newHashMap();
        Map<Long,Double> gmvMap= Maps.newHashMap();

        Double gmvValue=Double.parseDouble(gmv);
        Long monthId=null;
        Double monthPctTemp=0.00d;
        //每月的比例
        Double monthPct=0.00d;
        //每月的GMV
        double monthGmv=0.00d;
        //累计的比例
        double monthTotalPct =0;
        //累计的GMV
        double monthTotalGmv=0.00d;

        //有权重指数 且权重指数为12个月;
        if(null!=lastYearIndex&&lastYearIndex.size()==12)
        {
            for (WeightIndex weightIndex:lastYearIndex) {
                indexTotal=ArithUtil.add(indexTotal,weightIndex.getIndexValue());
            }

            //计算每个月占比、以及拆分到的GMV金额
            for (WeightIndex weightIndex:lastYearIndex) {
                monthId=Long.parseLong(year+String.valueOf(weightIndex.getMonthId()).substring(4,6));
                if(!weightIndex.getMonthId().equals(weightIndex.getYearId()+"12"))
                {

                    monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
                    //每月的指数/指数和*100 保留2位小数
                    monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);

                    monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
                    //计算GMV的占比
                    monthGmv=ArithUtil.formatDouble(ArithUtil.mul(gmvValue,monthPctTemp),0);
                    // ?? 观察 是否需要格式化一下
                    monthTotalGmv=ArithUtil.add(monthGmv,monthTotalGmv);

                    pctMap.put(monthId,monthPct);
                    gmvMap.put(monthId,monthGmv);

                }else //12月单独处理 采用1-前11个月  因为采用list 所以保证了12月是最后一个被处理的月份
                {
                    pctMap.put(monthId,ArithUtil.sub(100d,monthTotalPct));
                    gmvMap.put(monthId,ArithUtil.formatDouble( ArithUtil.sub(gmvValue,monthTotalGmv),0));
                }


            }


        }else  //按12个月平均
        {
            indexTotal=12d;
            for(Long mth:monthList)
            {
                if(!mth.equals(year+"12"))
                {
                    monthPctTemp=ArithUtil.div(1d,indexTotal,6);
                    monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);

                    monthTotalPct+=monthPct.intValue();

                    monthGmv=ArithUtil.formatDouble(ArithUtil.mul(gmvValue,monthPctTemp),0);
                    // ?? 观察 是否需要格式化一下
                    monthTotalGmv=ArithUtil.add(monthGmv,monthTotalGmv);

                    pctMap.put(mth,monthPct);
                    gmvMap.put(mth,monthGmv);
                }else
                {
                    pctMap.put(mth,100-monthTotalPct);
                    gmvMap.put(mth,ArithUtil.formatDouble( ArithUtil.sub(gmvValue,monthTotalGmv),0));
                }
            }
        }

        //获取上年各月的GMV值
        List<Map<String, Object>>  lastGmvMapList=yearHistoryMapper.getMonthGmvByYear(String.valueOf(Integer.parseInt(year)-1));
        Map<Long, Double> lastGmvMap=Maps.newHashMap();
        for(Map<String, Object> g:lastGmvMapList)
        {
            lastGmvMap.put(Long.parseLong(g.get("MONTH_ID").toString()),Double.parseDouble(g.get("GMV_VALUE").toString()));
        }

        Long lastMonthId= 0L;
        Double gmvTb=null;
        PlanDetail planDetail=null;
        for(Long mth:monthList)
        {
            lastMonthId=getLastMonthId(mth);
            planDetail = new PlanDetail();
            planDetail.setGmvValue(gmvMap.get(mth));
            planDetail.setYearId(Long.valueOf(year));
            planDetail.setMonthId(mth);
            planDetail.setGmvPct(pctMap.get(mth));
            planDetail.setPlanId(planId);

            //计算同比
            gmvTb=lastGmvMap.get(lastMonthId);
            if(null!=gmvTb)
            {
                planDetail.setGmvTbRate(computeTbRate(gmvTb,gmvMap.get(mth)));
                planDetail.setGmvTb(gmvTb);
            }

            planDetail.setIsHistory("N");
            planDetail.setCreateDt(new Date());
            planDetail.setUpdateDt(new Date());
            planDetailList.add(planDetail);
        }
        planDetailMapper.addPlanDetails(planDetailList);
    }

    @Override
    public void execute(String id) {
        gmvPlanMapper.updateStatus(id);
    }

    private void addPlan(String year, String gmv, String rate, Long planId,String forcastGmv,String forcastRate) {
        GmvPlan gmvPlan = new GmvPlan();
        gmvPlan.setYearId(Long.valueOf(year));
        gmvPlan.setGmvTarget(Double.valueOf(gmv));
        gmvPlan.setTargetRate(Double.valueOf(rate));
        gmvPlan.setPlanId(planId);
        gmvPlan.setForecastGmv(Double.valueOf(forcastGmv));
        gmvPlan.setForecastRate(Double.valueOf(forcastRate));
        gmvPlan.setStatus("D");
        gmvPlanMapper.insert(gmvPlan);
    }

    /**
     * 根据传入的月份ID 201901 返回上一年对应的月份ID
     * @param monthId
     * @return
     */
    private Long getLastMonthId(Long monthId)
    {
        String momthId=String.valueOf(monthId);

        return Long.parseLong((Integer.valueOf(momthId.substring(0,4))-1)+momthId.substring(4,6));
    }

    /**
     * 计算同比增长率  (本期-同期)/同期*100%
     * @param tbValue
     * @param currentValue
     * @return
     */
    private Double computeTbRate(Double tbValue,Double currentValue)
    {
        Double value=ArithUtil.mul( ArithUtil.div(ArithUtil.sub(currentValue,tbValue),tbValue,6),100d);
        return ArithUtil.formatDoubleByMode(value,2,RoundingMode.HALF_UP);
    }
}
