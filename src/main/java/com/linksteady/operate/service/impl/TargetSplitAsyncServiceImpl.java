package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.dao.TgtDismantMapper;
import com.linksteady.operate.dao.WeightIndexMapper;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TgtDismant;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.service.TargetSplitAsyncService;
import com.linksteady.operate.vo.Echart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
         log.info("开始对目标ID为 {} 的目标进行拆解!",targetId);
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
                splitByDay(targetInfo.getPeriodType(),targetInfo.getId(),targetInfo.getStartDt(),targetInfo.getEndDt(),targetInfo.getTargetVal(),targetInfo.getKpiCode());
            }

            //对任务进行计算
            calculateTarget(targetInfo);

            log.info("对目标ID为 {} 的目标拆解完成!",targetId);
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
    private void splitByYear(String periodType,long targetId,String year,double targetValue,String kpiCode) throws Exception
    {
        List<TgtDismant> targetDismantList = Lists.newArrayList();
        TgtDismant tgtDismant=null;

        List<WeightIndex> IndexList=weightIndexMapper.getWeightIndex("M",kpiCode);

        if(null==IndexList||IndexList.size()==0)
        {
            throw new Exception("权重指数不够进行拆分");
        }

        //累计百分比
        double monthTotalPct =0;
        //累计的目标值
        double monthTotalTarget=0.00d;

        //权重指数和
        double indexTotal=IndexList.stream().mapToDouble(WeightIndex::getIndexValue).sum();

        //计算每个月占比、以及拆分到的目标值
        for (WeightIndex weightIndex:IndexList) {
            String monthId=year+"-"+weightIndex.getPeriodId();

            tgtDismant = new TgtDismant();
            tgtDismant.setTgtId(targetId);
            tgtDismant.setPeriodType(periodType);
            tgtDismant.setPeriodDate(monthId);
            tgtDismant.setComputeDt(new Date());
            tgtDismant.setActualVal(0d);
            tgtDismant.setTgtWeightIdx(weightIndex.getIndexValue());

            if(!"12".equals(weightIndex.getPeriodId()))
            {
                //每月的权重指数占比
                double monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
                //占比*100 保留2位小数
                double monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);

                //累计的百分比
                monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
                //计算目标分摊到当月的值
                double monthTarget=ArithUtil.formatDouble(ArithUtil.mul(targetValue,monthPctTemp),0);
                //累计目标值
                monthTotalTarget=ArithUtil.add(monthTarget,monthTotalTarget);

                tgtDismant.setTgtVal(monthTarget);
                tgtDismant.setTgtPercent(monthPct);

            }else //12月单独处理 采用1-前11个月  因为采用list 所以保证了12月是最后一个被处理的月份
            {
                tgtDismant.setTgtVal(ArithUtil.formatDouble( ArithUtil.sub(targetValue,monthTotalTarget),0));
                tgtDismant.setTgtPercent(ArithUtil.formatDoubleByMode(ArithUtil.sub(100d,monthTotalPct),2,RoundingMode.DOWN));
            }

            targetDismantList.add(tgtDismant);
        }

        tgtDismantMapper.saveTargetDismant(targetDismantList);
    }

    /**
     * 按天拆分目标值
     * @param periodType
     * @param targetId
     * @param startDt
     * @param endDt
     * @param targetValue
     * @param kpiCode
     */
    private void splitByDay(String periodType,long targetId,String startDt,String endDt,double targetValue,String kpiCode) throws Exception
    {
        List<TgtDismant> targetDismantList = Lists.newArrayList();
        TgtDismant tgtDismant=null;

        //获取中每天的权重指数  按权重指数进行拆解
        List<WeightIndex> IndexList=weightIndexMapper.getWeightIndexByDay(startDt.replace("-",""),endDt.replace("-",""),kpiCode);

        if(null==IndexList||IndexList.size()==0)
        {
            throw new Exception("权重指数不够进行拆分");
        }

        //权重指数和
        double indexTotal=IndexList.stream().mapToDouble(WeightIndex::getIndexValue).sum();

        //累计的比例
        double monthTotalPct =0;
        //累计的目标值
        double monthTotalTarget=0.00d;

        //计算每个月占比、以及拆分到的目标值
        for (WeightIndex weightIndex:IndexList) {
            String day=weightIndex.getPeriodId();
            tgtDismant = new TgtDismant();
            tgtDismant.setTgtId(targetId);
            tgtDismant.setPeriodType(periodType);
            tgtDismant.setPeriodDate(day);
            tgtDismant.setComputeDt(new Date());
            tgtDismant.setActualVal(0d);
            tgtDismant.setTgtWeightIdx(weightIndex.getIndexValue());

            //如果不是最后一个元素
            if(!IndexList.get(IndexList.size()-1).getPeriodId().equals(weightIndex.getPeriodId()))
            {
                //每月的权重指数占比
                double  monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
                //占比*100 保留2位小数
                double monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);

                //累计的百分比
                monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
                //计算目标分摊到当月的值
                double monthTarget=ArithUtil.formatDouble(ArithUtil.mul(targetValue,monthPctTemp),0);
                //累计目标值
                monthTotalTarget=ArithUtil.add(monthTarget,monthTotalTarget);

                tgtDismant.setTgtVal(monthTarget);
                tgtDismant.setTgtPercent(monthPct);
            }else //最后一个元素
            {
                tgtDismant.setTgtVal(ArithUtil.formatDouble(ArithUtil.sub(targetValue,monthTotalTarget),0));
                tgtDismant.setTgtPercent(ArithUtil.formatDoubleByMode(ArithUtil.sub(100d,monthTotalPct),2,RoundingMode.DOWN));
            }

            targetDismantList.add(tgtDismant);

        }
        tgtDismantMapper.saveTargetDismant(targetDismantList);
    }

    /**
     * 按月拆分目标值
     * @param periodType
     * @param targetId
     * @param month
     * @param targetValue
     * @param kpiCode
     */
    private void splitByMonth(String periodType,long targetId,String month,double targetValue,String kpiCode)  throws Exception
    {
        List<TgtDismant> targetDismantList = Lists.newArrayList();
        TgtDismant tgtDismant=null;

        //获取中每天的权重指数  按权重指数进行拆解
        List<WeightIndex> IndexList=weightIndexMapper.getWeightIndexByMonth(month,kpiCode);

        if(null==IndexList||IndexList.size()==0)
        {
            throw new Exception("权重指数不够进行拆分");
        }

        //权重指数和
        double indexTotal=IndexList.stream().mapToDouble(WeightIndex::getIndexValue).sum();

        //累计的比例
        double monthTotalPct =0;
        //累计的目标值
        double monthTotalTarget=0.00d;

        //计算每个月占比、以及拆分到的目标值
        for (WeightIndex weightIndex:IndexList) {
            String day=weightIndex.getPeriodId();
            tgtDismant = new TgtDismant();
            tgtDismant.setTgtId(targetId);
            tgtDismant.setPeriodType(periodType);
            tgtDismant.setPeriodDate(day);
            tgtDismant.setComputeDt(new Date());
            tgtDismant.setActualVal(0d);
            tgtDismant.setTgtWeightIdx(weightIndex.getIndexValue());

            //如果不是最后一个元素
            if(!IndexList.get(IndexList.size()-1).getPeriodId().equals(weightIndex.getPeriodId()))
            {
                //每月的权重指数占比
                double  monthPctTemp=ArithUtil.div(weightIndex.getIndexValue(),indexTotal,10);
                //占比*100 保留2位小数
                double monthPct=ArithUtil.formatDoubleByMode(ArithUtil.mul(monthPctTemp,100d),2, RoundingMode.DOWN);

                //累计的百分比
                monthTotalPct=ArithUtil.add(monthTotalPct,monthPct);
                //计算目标分摊到当月的值
                double monthTarget=ArithUtil.formatDouble(ArithUtil.mul(targetValue,monthPctTemp),0);
                //累计目标值
                monthTotalTarget=ArithUtil.add(monthTarget,monthTotalTarget);

                tgtDismant.setTgtVal(monthTarget);
                tgtDismant.setTgtPercent(monthPct);
            }else //最后一个元素
            {
                tgtDismant.setTgtVal(ArithUtil.formatDouble( ArithUtil.sub(targetValue,monthTotalTarget),0));
                tgtDismant.setTgtPercent(ArithUtil.formatDoubleByMode(ArithUtil.sub(100d,monthTotalPct),2,RoundingMode.DOWN));
            }

            targetDismantList.add(tgtDismant);

        }
        tgtDismantMapper.saveTargetDismant(targetDismantList);
    }

    /**
     * 针对每个目标进行运算
     * @param targetInfo
     */
    private void calculateTarget(TargetInfo targetInfo)
    {
        //todo 对目前执行情况进行计算
        log.info("开始对目标ID为 {} 的目标进行计算!",targetInfo.getId());

        //计算头信息

        //计算完成信息

        log.info("对目标ID为 {} 的目标计算完成!",targetInfo.getId());
        targetListMapper.updateTargetStatus(targetInfo.getId(),"2");

    }
}
