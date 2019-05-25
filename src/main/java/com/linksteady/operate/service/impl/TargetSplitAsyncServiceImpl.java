package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.dao.TgtDismantMapper;
import com.linksteady.operate.dao.WeightIndexMapper;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TgtDismant;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.service.TargetSplitAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 目标进行分解的异步任务
 * @author  huang
 */
@Slf4j
@Service
public class TargetSplitAsyncServiceImpl implements TargetSplitAsyncService {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    private WeightIndexMapper weightIndexMapper;

    @Autowired
    private TgtDismantMapper tgtDismantMapper;

    private static final String TARGET_PERIOD_YEAR="year";
    private static final String TARGET_PERIOD_MONTH="month";
    private static final String TARGET_PERIOD_DAY="day";

    @Async
    @Override
    public void targetSplit(Long targetId) {
        try{
            //获取目标的详细信息
            TargetInfo targetInfo=targetListMapper.selectByPrimaryKey(targetId);

            //如果是年，则拆分到12个月  //如果是月，则拆分到天  如果是天到天，则拆分到每天
            if(TARGET_PERIOD_YEAR.equals(targetInfo.getPeriodType()))
            {
                splitByYear(targetInfo.getPeriodType(),targetInfo.getId(),targetInfo.getStartDt(),targetInfo.getTargetVal(),targetInfo.getKpiCode());
            }else if(TARGET_PERIOD_MONTH.equals(targetInfo.getPeriodType()))
            {
                splitByMonth(targetInfo.getPeriodType(),targetInfo.getId(),targetInfo.getStartDt(),targetInfo.getTargetVal(),targetInfo.getKpiCode());
            }else if(TARGET_PERIOD_DAY.equals(targetInfo.getPeriodType()))
            {
                splitByDay(targetInfo.getPeriodType(),targetInfo.getId(),targetInfo.getStartDt(),targetInfo.getTargetVal(),targetInfo.getKpiCode());
            }

            //对任务进行计算
            calculateTarget(targetInfo);

        }catch (Exception e)
        {
             log.error("ID: {} 拆分计算任务异常",targetId,e);
             //更新目标的状态为错误状态
            targetListMapper.updateTargetStatus(targetId,"-1");
        }
    }

    @Override
    public Map<String, Object> getDismantData(Long targetId) {
        List<TgtDismant> dataList = tgtDismantMapper.findByTgtId(targetId);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("分解目标");
        List<String> xdata = dataList.stream().map(x->x.getPeriodDate()).collect(Collectors.toList());
        echart.setxAxisData(xdata);
        echart.setyAxisData(dataList.stream().map(x->String.valueOf(x.getTgtVal())).collect(Collectors.toList()));
        List<Double> row1 = dataList.stream().map(x->x.getTgtVal()).collect(Collectors.toList());
        List<Double> row2 = dataList.stream().map(x->x.getTgtWeightIdx()).collect(Collectors.toList());
        List<Double> row3 = dataList.stream().map(x->x.getTgtPercent()).collect(Collectors.toList());
        Map<String, Object> result = Maps.newHashMap();
        result.put("chart", echart);
        result.put("head", xdata);
        result.put("row1", row1);
        result.put("row2", row2);
        result.put("row3", row3);
        return result;
    }


    /**
     * 按年拆分目标值
     * @param periodType
     * @param targetId
     * @param year
     * @param targetValue
     * @param kpiCode
     */
    private void splitByYear(String periodType,long targetId,String year,double targetValue,String kpiCode)
    {
        List<TgtDismant> targetDismantList = Lists.newArrayList();

        List<Long>  monthList=new ArrayList(){{
            for(int i=1;i<=12;i++)
            {
                this.add(Long.parseLong(year+ String.format("%02d", i)));
            }
        }};

        //获取上一年的权重指数  按权重指数进行拆解
        List<WeightIndex> lastYearIndex=weightIndexMapper.getWeightIndex(String.valueOf(Integer.parseInt(year)-1),kpiCode.toUpperCase());

        //权重指数和
        Double indexTotal=0d;
        Map<Long,Double> pctMap= Maps.newHashMap();
        Map<Long,Double> valueMap= Maps.newHashMap();
        //存放权重指数
        Map<Long,Double> indexMap= Maps.newHashMap();

        Double monthPctTemp=0.00d;
        //每月的比例
        Double monthPct=0.00d;
        //每月的GMV
        double monthTarget=0.00d;
        //累计的比例
        double monthTotalPct =0;
        //累计的目标值
        double monthTotalTarget=0.00d;

        //有权重指数 且权重指数为12个月;
        if(null!=lastYearIndex&&lastYearIndex.size()==12)
        {
            for (WeightIndex weightIndex:lastYearIndex) {
                indexTotal= ArithUtil.add(indexTotal,weightIndex.getIndexValue());
            }

            //计算每个月占比、以及拆分到的目标值
            for (WeightIndex weightIndex:lastYearIndex) {
                long monthId=Long.parseLong(year+String.valueOf(weightIndex.getMonthId()).substring(4,6));
                if(!weightIndex.getMonthId().equals(weightIndex.getYearId()+"12"))
                {
                    //每月的权重指数占比
                    monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
                    //占比*100 保留2位小数
                    monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);

                    //累计的百分比
                    monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
                    //计算目标分摊到当月的值
                    monthTarget=ArithUtil.formatDouble(ArithUtil.mul(targetValue,monthPctTemp),0);
                    //累计目标值
                    monthTotalTarget=ArithUtil.add(monthTarget,monthTotalTarget);

                    pctMap.put(monthId,monthPct);
                    valueMap.put(monthId,monthTarget);

                }else //12月单独处理 采用1-前11个月  因为采用list 所以保证了12月是最后一个被处理的月份
                {
                    pctMap.put(monthId,ArithUtil.sub(100d,monthTotalPct));
                    valueMap.put(monthId,ArithUtil.formatDouble( ArithUtil.sub(targetValue,monthTotalTarget),0));
                }
                indexMap.put(monthId,weightIndex.getIndexValue());

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

                    monthTarget=ArithUtil.formatDouble(ArithUtil.mul(targetValue,monthPctTemp),0);
                    // ?? 观察 是否需要格式化一下
                    monthTotalTarget=ArithUtil.add(monthTarget,monthTotalTarget);

                    pctMap.put(mth,monthPct);
                    valueMap.put(mth,monthTarget);
                }else
                {
                    pctMap.put(mth,100-monthTotalPct);
                    valueMap.put(mth,ArithUtil.formatDouble( ArithUtil.sub(targetValue,monthTotalTarget),0));
                }
                indexMap.put(mth,1d);
            }
        }


        TgtDismant tgtDismant=null;
        for(Long mth:monthList)
        {
            tgtDismant = new TgtDismant();
            tgtDismant.setTgtVal(valueMap.get(mth));
            tgtDismant.setTgtPercent(pctMap.get(mth));
            tgtDismant.setTgtId(targetId);
            tgtDismant.setPeriodType(periodType);
            tgtDismant.setPeriodDate(String.valueOf(mth));
            tgtDismant.setComputeDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            tgtDismant.setActualVal(0d);
            tgtDismant.setTgtWeightIdx(indexMap.get(mth));

            targetDismantList.add(tgtDismant);
        }

        tgtDismantMapper.saveTargetDismant(targetDismantList);
    }

    /**
     * 按天拆分目标值
     * @param periodType
     * @param targetId
     * @param month
     * @param targetValue
     * @param kpiCode
     */
    private void splitByDay(String periodType,long targetId,String month,double targetValue,String kpiCode)
    {
//        List<TgtDismant> targetDismantList = Lists.newArrayList();
//
//        List<Long>  monthList=new ArrayList(){{
//            for(int i=1;i<=12;i++)
//            {
//                this.add(Long.parseLong(year+ String.format("%02d", i)));
//            }
//        }};
//
//        //获取上一年的权重指数  //gmv按权重指数进行拆解
//        List<WeightIndex> lastYearIndex=weightIndexMapper.getWeightIndex(String.valueOf(Integer.parseInt(year)-1));
//
//        //权重指数和
//        Double indexTotal=0d;
//        Map<Long,Double> pctMap= Maps.newHashMap();
//        Map<Long,Double> gmvMap= Maps.newHashMap();
//
//        Double gmvValue=targetValue;
//
//        Long monthId=null;
//        Double monthPctTemp=0.00d;
//        //每月的比例
//        Double monthPct=0.00d;
//        //每月的GMV
//        double monthGmv=0.00d;
//        //累计的比例
//        double monthTotalPct =0;
//        //累计的GMV
//        double monthTotalGmv=0.00d;
//
//        //有权重指数 且权重指数为12个月;
//        if(null!=lastYearIndex&&lastYearIndex.size()==12)
//        {
//            for (WeightIndex weightIndex:lastYearIndex) {
//                indexTotal= ArithUtil.add(indexTotal,weightIndex.getIndexValue());
//            }
//
//            //计算每个月占比、以及拆分到的目标值
//            for (WeightIndex weightIndex:lastYearIndex) {
//                monthId=Long.parseLong(year+String.valueOf(weightIndex.getMonthId()).substring(4,6));
//                if(!weightIndex.getMonthId().equals(weightIndex.getYearId()+"12"))
//                {
//
//                    monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
//                    //每月的指数/指数和*100 保留2位小数
//                    monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);
//
//                    monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
//                    //计算GMV的占比
//                    monthGmv=ArithUtil.formatDouble(ArithUtil.mul(gmvValue,monthPctTemp),0);
//                    // ?? 观察 是否需要格式化一下
//                    monthTotalGmv=ArithUtil.add(monthGmv,monthTotalGmv);
//
//                    pctMap.put(monthId,monthPct);
//                    gmvMap.put(monthId,monthGmv);
//
//                }else //12月单独处理 采用1-前11个月  因为采用list 所以保证了12月是最后一个被处理的月份
//                {
//                    pctMap.put(monthId,ArithUtil.sub(100d,monthTotalPct));
//                    gmvMap.put(monthId,ArithUtil.formatDouble( ArithUtil.sub(gmvValue,monthTotalGmv),0));
//                }
//            }
//        }else  //按12个月平均
//        {
//            indexTotal=12d;
//            for(Long mth:monthList)
//            {
//                if(!mth.equals(year+"12"))
//                {
//                    monthPctTemp=ArithUtil.div(1d,indexTotal,6);
//                    monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);
//
//                    monthTotalPct+=monthPct.intValue();
//
//                    monthGmv=ArithUtil.formatDouble(ArithUtil.mul(gmvValue,monthPctTemp),0);
//                    // ?? 观察 是否需要格式化一下
//                    monthTotalGmv=ArithUtil.add(monthGmv,monthTotalGmv);
//
//                    pctMap.put(mth,monthPct);
//                    gmvMap.put(mth,monthGmv);
//                }else
//                {
//                    pctMap.put(mth,100-monthTotalPct);
//                    gmvMap.put(mth,ArithUtil.formatDouble( ArithUtil.sub(gmvValue,monthTotalGmv),0));
//                }
//            }
//        }
//
//
//        TgtDismant tgtDismant=null;
//        for(Long mth:monthList)
//        {
//            tgtDismant = new TgtDismant();
//            tgtDismant.setTgtVal(gmvMap.get(mth));
//            tgtDismant.setTgtPercent(pctMap.get(mth));
//            tgtDismant.setTgtId(targetId);
//            tgtDismant.setPeriodType(periodType);
//            tgtDismant.setPeriodDate(year);
//            tgtDismant.setComputeDt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            tgtDismant.setActualVal(0d);
//
//            targetDismantList.add(tgtDismant);
//        }

//        tgtDismantMapper.saveTargetDismant(targetDismantList);
    }

    /**
     * 按月拆分目标值
     * @param periodType
     * @param targetId
     * @param month
     * @param targetValue
     * @param kpiCode
     */
    private void splitByMonth(String periodType,long targetId,String month,double targetValue,String kpiCode)
    {
        List<TgtDismant> targetDismantList = Lists.newArrayList();
        //获取当月中所有的天
        List<String> periodList= DateUtil.getEveryday(month+"-01",DateUtil.getLastDayOfMonth(month));

//        //获取当前指标在所有天上的权重指数
//        List<WeightIndex> monthIndex=getIndexWeight(month+"-01",DateUtil.getLastDayOfMonth(month),kpiCode);
//
//        //权重指数和
//        Double indexTotal=0d;
//        Map<Long,Double> pctMap= Maps.newHashMap();
//        Map<Long,Double> targetValueMap= Maps.newHashMap();
//
//        //累计的比例
//        double monthTotalPct =0;
//        //累计的GMV
//        double monthTotalGmv=0.00d;
//
//        //按权重指数进行拆分
//
//        //获取权重指数的和
//        for (WeightIndex weightIndex:monthIndex) {
//            indexTotal= ArithUtil.add(indexTotal,weightIndex.getIndexValue());
//        }
//
//        //计算每天的占比、以及拆分到的目标值
//        for (WeightIndex weightIndex:monthIndex) {
//
//            String period=weightIndex.getPeriod();
//            if(!weightIndex.getMonthId().equals(weightIndex.getYearId()+"12"))
//            {
//               //每月的权重指数占比
//                Double monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
//                //占比*100 保留2位小数
//                Double monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);
//
//                monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
//
//                //计算指标值的的占比
//                double targetValuePct=ArithUtil.formatDouble(ArithUtil.mul(targetValue,monthPctTemp),0);
//                // 指标的累计值
//                monthTotalGmv=ArithUtil.add(targetValuePct,monthTotalGmv);
//
//                pctMap.put(period,monthPct);
//                targetValueMap.put(period,targetValuePct);
//
//            }else //最后一天单独处理 采用1-前面所有的天  因为采用list 所以保证了最后一天是最后被处理的
//            {
//                pctMap.put(monthId,ArithUtil.sub(100d,monthTotalPct));
//                targetValueMap.put(monthId,ArithUtil.formatDouble( ArithUtil.sub(gmvValue,monthTotalGmv),0));
//            }
//        }
//
//
//        TgtDismant tgtDismant=null;
//        for(String period:periodList)
//        {
//            tgtDismant = new TgtDismant();
//            tgtDismant.setTgtVal(targetValueMap.get(period));
//            tgtDismant.setTgtPercent(pctMap.get(period));
//            tgtDismant.setTgtId(targetId);
//            tgtDismant.setPeriodType(periodType);
//            tgtDismant.setPeriodDate(month);
//            tgtDismant.setComputeDt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            tgtDismant.setActualVal(0d);
//
//            targetDismantList.add(tgtDismant);
//        }
        tgtDismantMapper.saveTargetDismant(targetDismantList);
    }

    private Map<String,Double> getIndexWeight(String startDt,String endDt,String kpiCode)
    {
        return null;
    }

    private void calculateTarget(TargetInfo targetInfo)
    {
        //todo 对目前执行情况进行计算

        targetListMapper.updateTargetStatus(targetInfo.getId(),"2");
    }
}
