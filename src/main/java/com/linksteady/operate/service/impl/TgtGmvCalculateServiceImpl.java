package com.linksteady.operate.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.StringTemplate;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.dao.TgtDismantMapper;
import com.linksteady.operate.domain.TargetDimension;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TgtDismant;
import com.linksteady.operate.domain.TgtReference;
import com.linksteady.operate.service.TgtCalculateService;
import com.linksteady.operate.vo.DimJoinVO;
import com.linksteady.operate.vo.TemplateResult;
import com.linksteady.operate.vo.TgtReferenceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TgtGmvCalculateServiceImpl implements TgtCalculateService {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    TgtDismantMapper tgtDismantMapper;

    private static final String TARGET_PERIOD_YEAR="year";
    private static final String TARGET_PERIOD_MONTH="month";
    private static final String TARGET_PERIOD_DAY="day";

    /**
     * 获取GMV的历史参考值
     * @param period
     * @param startDt
     * @param endDt
     * @param dimInfo
     * @return
     */
    @Override
    public List<TgtReferenceVO> getReferenceData(String period, String startDt, String endDt, Map<String, String> dimInfo) {

        List<TgtReferenceVO> result= Lists.newLinkedList();
        //年
        if(TARGET_PERIOD_YEAR.equals(period))
        {
            //获取过去4年的周期列表
            List<String> periodList= Arrays.asList(String.valueOf(Long.valueOf(startDt)-1),String.valueOf(Long.valueOf(startDt)-2),String.valueOf(Long.valueOf(startDt)-3),String.valueOf(Long.valueOf(startDt)-4));
            List<TgtReference> data=getGmvHistoryByYear(periodList,dimInfo);

            Map<String,Double> dataMap=data.stream().collect(Collectors.toMap(TgtReference::getPeriodName,TgtReference::getValue));

            //计算同比信息
            for(int i=0;i<periodList.size()-1;i++)
            {
                //获取当年的值
                double current= Optional.ofNullable(dataMap.get(periodList.get(i))).orElse(0d);
                //获取上一年的值
                double pre= Optional.ofNullable(dataMap.get(periodList.get(i+1))).orElse(0d);
                //计算同比增长率
                double tb=pre==0?0: ArithUtil.formatDoubleByMode((current-pre)/pre*100,2, RoundingMode.DOWN);

                //如果当年的值为0，则不计入参照
                if(current!=0)
                {
                    TgtReferenceVO vo=new TgtReferenceVO();
                    vo.setPeriod(periodList.get(i));
                    vo.setKpi(String.valueOf(current));
                    if(tb!=0)
                    {
                        vo.setYearOnYear(String.valueOf(tb));
                    }

                    result.add(vo);
                }

            }

        }else if(TARGET_PERIOD_MONTH.equals(period))
        {
            //获取当前所有的月
            YearMonth yearMonth = YearMonth.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth preYearMonth=yearMonth.minus(Period.ofYears(1));

            //当前月份 过去的4个月的列表
            List<YearMonth> periodList=Arrays.asList(yearMonth.minus(Period.ofMonths(1)),yearMonth.minus(Period.ofMonths(2)),yearMonth.minus(Period.ofMonths(3)),yearMonth.minus(Period.ofMonths(4)));

            //过去三个月对应的去年同期的月份列表
            List<YearMonth> tbPeroidList=Arrays.asList(preYearMonth.minus(Period.ofMonths(1)),preYearMonth.minus(Period.ofMonths(2)),preYearMonth.minus(Period.ofMonths(3)));

            List<YearMonth> allPeriodList=Lists.newArrayList();
            allPeriodList.addAll(periodList);
            allPeriodList.addAll(tbPeroidList);

            List<String> allPeriodList2=allPeriodList.stream().map(s->s.toString().replace("-","")).collect(Collectors.toList());
            List<TgtReference> data=getGmvHistoryByMonth(allPeriodList2,dimInfo);

            Map<String,Double> dataMap=data.stream().collect(Collectors.toMap(TgtReference::getPeriodName,TgtReference::getValue));

            //计算同比和环比值
            for(int j=0;j<periodList.size()-1;j++)
            {
                //当月名称
                String currentMonthName=periodList.get(j).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                //上月名称
                String lastMonthName=periodList.get(j).minus(Period.ofMonths(1)).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                //去年同期月份名称
                String preMonthName=periodList.get(j).minus(Period.ofMonths(1)).format(DateTimeFormatter.ofPattern("yyyy-MM"));

                double currentValue= Optional.ofNullable(dataMap.get(currentMonthName)).orElse(0d);
                double lastMonthValue= Optional.ofNullable(dataMap.get(lastMonthName)).orElse(0d);
                //去年同期值
                double preMonthValue= Optional.ofNullable(dataMap.get(preMonthName)).orElse(0d);

                //计算同比增长率
                double tb=preMonthValue==0?0: ArithUtil.formatDoubleByMode((currentValue-preMonthValue)/preMonthValue*100,2, RoundingMode.DOWN);
                //环比
                double hb=lastMonthValue==0?0: ArithUtil.formatDoubleByMode((currentValue-lastMonthValue)/lastMonthValue*100,2, RoundingMode.DOWN);

                //如果当年的值为0，则不计入参照
                if(currentValue!=0)
                {
                    TgtReferenceVO vo=new TgtReferenceVO();
                    vo.setPeriod(periodList.get(j).format(DateTimeFormatter.ofPattern("yyyy年MM月")));
                    vo.setKpi(String.valueOf(currentValue));
                    if(tb!=0)
                    {
                        vo.setYearOnYear(String.valueOf(tb));
                    }
                    if(hb!=0)
                    {
                        vo.setYearOverYear(String.valueOf(hb));
                    }

                    result.add(vo);
                }

            }
        }else if(TARGET_PERIOD_DAY.equals(period))
        {
            //获取开始时间 和 结束时间
            LocalDate startLocalDate = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endLocalDate = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            //上一周期的gmv值
            TgtReference prePeriodValue=null;
            TgtReference currPeriodValue=null;
            //获取上一年的开始时间和结束时间
            for(int k=4;k>=0;k--)
            {
                LocalDate tempStartDate=startLocalDate.minusYears(k);
                LocalDate tempEndDate=endLocalDate.minusYears(k);
                //最早的那次取值 仅仅是为了计算第三次的同比而取的
                if(k==4)
                {
                    prePeriodValue=getGmvHistoryByPeriodDay(tempStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),tempEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),dimInfo);
                }else
                {
                    //当前周期的值
                    currPeriodValue=getGmvHistoryByPeriodDay(tempStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),tempEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),dimInfo);

                    double prevalue=null==prePeriodValue?0:prePeriodValue.getValue();
                    double curvalue=null==currPeriodValue?0:currPeriodValue.getValue();
                    if(curvalue!=0)
                    {
                        TgtReferenceVO vo=new TgtReferenceVO();
                        vo.setPeriod(tempStartDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))+"-"+tempEndDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
                        vo.setKpi(String.valueOf(curvalue));
                        double tb=prevalue==0?0: ArithUtil.formatDoubleByMode((curvalue-prevalue)/prevalue*100,2, RoundingMode.DOWN);
                        if(tb!=0)
                        {
                            vo.setYearOnYear(String.valueOf(tb));
                        }
                        result.add(vo);
                    }

                    //将当次的值设置为上一次的值
                    prePeriodValue=currPeriodValue;
                }
            }
        }
        return result;
    }


    /**
     * 对某个target 进行计算（kpi为gmv)
     *
     */
    @Override
    public void calculateTarget(TargetInfo targetInfo) {
        log.info("开始对目标ID为 {} 的目标进行计算!",targetInfo.getId());

        Long targetId=targetInfo.getId();

        Map<String,String> diminfo=null;
        if(null!=targetInfo.getDimensionList()&&targetInfo.getDimensionList().size()>0)
        {
            diminfo=targetInfo.getDimensionList().stream().collect(Collectors.toMap(TargetDimension::getDimensionCode,TargetDimension::getDimensionValCode));
        }else
        {
            diminfo= Maps.newHashMap();
        }

        //实际完成的列表
        List<TgtReference> actualResult=null;
        //去年同期的情况
        List<TgtReference> lastResult=null;
        //截止日期
        long closeDate=0L;
        //截止日期
        String currentDt="";

        //获取实际执行情况 年，则获取12个月的情况
        if(TARGET_PERIOD_YEAR.equals(targetInfo.getPeriodType()))
        {
            actualResult=getGmvHistoryByMonth(targetInfo.getStartDt(),diminfo);
            String preYear=String.valueOf(Long.valueOf(targetInfo.getStartDt())-1);
            lastResult=getGmvHistoryByMonth(preYear,diminfo);

            //当前年的最后一天
            closeDate=Long.parseLong(targetInfo.getStartDt()+"1231");
            //当前月
            currentDt=LocalDate.now().format( DateTimeFormatter.ofPattern("yyyy-MM"));
        }else if(TARGET_PERIOD_MONTH.equals(targetInfo.getPeriodType()))
        {
            actualResult=getGmvHistoryByDay(targetInfo.getStartDt().replace("-",""),diminfo);
            YearMonth yearMonth = YearMonth.parse(targetInfo.getStartDt(), DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth preYearMonth=yearMonth.minus(Period.ofYears(1));
            lastResult=getGmvHistoryByDay(preYearMonth.toString().replace("-",""),diminfo);

            //当月的最后一天
            LocalDate lastday =LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            closeDate=Long.parseLong(lastday.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            //当天
            currentDt=LocalDate.now().format( DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else if(TARGET_PERIOD_DAY.equals(targetInfo.getPeriodType()))
        {
            LocalDate startLocalDate = LocalDate.parse(targetInfo.getStartDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endLocalDate = LocalDate.parse(targetInfo.getEndDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate tempStartDate=startLocalDate.minusYears(1);
            LocalDate tempEndDate=endLocalDate.minusYears(1);

            actualResult=getGmvHistoryByDay(tempStartDate.toString().replace("-",""),targetInfo.getEndDt().replace("-",""),diminfo);
            lastResult=getGmvHistoryByDay(tempEndDate.toString().replace("-",""),targetInfo.getEndDt().replace("-",""),diminfo);

            //结束天
            closeDate=Long.parseLong(targetInfo.getEndDt().replace("-",""));

            //当天
            currentDt=LocalDate.now().format( DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }



        //计算实际的汇总值
        double actualTotal=actualResult.stream().mapToDouble(TgtReference::getValue).sum();

        //实际值去年同比、实际值去年同期
        double lastTotal=lastResult.stream().mapToDouble(TgtReference::getValue).sum();
        //如果去年值为0 则同比值展示为null
        Double actualTb=lastTotal==0?null:ArithUtil.formatDoubleByMode((actualTotal-lastTotal)/lastTotal*100,2,RoundingMode.DOWN);

        //完成率、剩余目标、业绩缺口
        double finishRate=ArithUtil.formatDoubleByMode(targetInfo.getTargetVal()==0?0:actualTotal/targetInfo.getTargetVal()*100,2,RoundingMode.HALF_UP);
        //剩余目标
        double remainTgt=ArithUtil.formatDoubleByMode(targetInfo.getTargetVal()-actualTotal,2,RoundingMode.DOWN);

        //本年迄今为止的变异系数、去年同期变异系数
        //均值
        double actualavg=actualResult.stream().mapToDouble(TgtReference::getValue).average().orElse(0d);
        double lastavg=lastResult.stream().mapToDouble(TgtReference::getValue).average().orElse(0d);

        double actualStand= DataStatisticsUtils.getStandardDevition(actualResult.stream().mapToDouble(TgtReference::getValue).toArray());
        double lastStand=DataStatisticsUtils.getStandardDevition(lastResult.stream().mapToDouble(TgtReference::getValue).toArray());

        Double actualCov=actualavg==0?null:ArithUtil.formatDoubleByMode(actualStand/actualavg*100,2,RoundingMode.DOWN);
        Double lastCov=lastavg==0?null:ArithUtil.formatDoubleByMode(lastStand/lastavg*100,2,RoundingMode.DOWN);

        //更新头表
        targetInfo.setActualVal(actualTotal);
        targetInfo.setActualValLast(lastTotal);
        targetInfo.setActualValRate(actualTb);
        targetInfo.setFinishRate(finishRate);
        //如果剩余待完成业绩小于0，则说明超额完成了 这时候显示为0
        if(remainTgt<0)
        {
            targetInfo.setRemainTgt(0d);
        }else
        {
            targetInfo.setRemainTgt(remainTgt);
        }

        targetInfo.setVaryIdx(actualCov);
        targetInfo.setVaryIdxLast(lastCov);
        targetInfo.setComputeDt(new Date());
        targetListMapper.updateTargetActualValue(targetInfo);

        List<TgtDismant> tgtList=Lists.newArrayList();
        //计算拆解明细表的实际值和去年指标值
        //遍历实际值列表
        Map<String,Double> actualMap=actualResult.stream().collect(Collectors.toMap(TgtReference::getPeriodName,TgtReference::getValue));
        Map<String,Double> lastMap=lastResult.stream().collect(Collectors.toMap(TgtReference::getPeriodName,TgtReference::getValue));
        for(TgtReference tgtReference:actualResult)
        {
            TgtDismant tgtDismant=new TgtDismant();
            tgtDismant.setTgtId(targetInfo.getId());
            tgtDismant.setPeriodDate(tgtReference.getPeriodName());
            tgtDismant.setActualVal(Optional.ofNullable(actualMap.get(tgtReference.getPeriodName())).orElse(0d));
            tgtDismant.setComputeDt(new Date());
            tgtDismant.setActualValLast(Optional.ofNullable(lastMap.get(tgtReference.getPeriodName())).orElse(0d));

            tgtList.add(tgtDismant);
        }

        tgtDismantMapper.updateTgtDismantBatch(tgtList);

        //更新是否已经 过时 的标志
        tgtDismantMapper.updateTgtDismantPastFlag(targetId,currentDt);

        //更新未完成 的标志
        tgtDismantMapper.updateTgtDismantFinishFlag(targetId);

        //更新未完成的周期目标个数和业绩缺口
        if(remainTgt<0)
        {   //当前已经超额完成，则更新业绩缺口数为0
            targetListMapper.updateFinshDiffWithZero(targetId);

        }else{
            targetListMapper.updateFinshDiff(targetId);
        }


        //更新环比增长率(获取到所有已经过时的拆解明细 然后计算完成率)
        List<TgtDismant> pastList=tgtDismantMapper.getPastDismantInfo(targetId);

        List<TgtDismant> growthRateList=Lists.newArrayList();
        TgtDismant growthRate=null;

        int i=0;
        double prePeriodValue=0d;
        for(TgtDismant tgtDismant:pastList)
        {
            if(i==0)
            {
                prePeriodValue=tgtDismant.getActualVal();
            }else
            {
                double currentPeriodValue=tgtDismant.getActualVal();
                //计算环比值
                Double growthRateValue=prePeriodValue==0?null:ArithUtil.formatDoubleByMode((currentPeriodValue-prePeriodValue)/prePeriodValue*100,2,RoundingMode.DOWN);

                if(null!=growthRateValue)
                {
                    growthRate=new TgtDismant();
                    growthRate.setId(tgtDismant.getId());
                    growthRate.setGrowthRate(growthRateValue);
                    growthRateList.add(growthRate);
                }
                prePeriodValue=currentPeriodValue;
            }
        }
        //更新环比增长率
        if(growthRateList.size()>0)
        {
            tgtDismantMapper.updateGrowthRate(growthRateList);
        }


        //判断此次计算完之后是否为失效状态(目标截止日期： 如果周期为年，则为当年最后一天 月为当月最后一天，或天-天的截止天数，当天距离目标的截止日期为三天，则标记为失效。)
       long nowdt=Long.parseLong(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
       if(nowdt-closeDate>=3)
       {
           targetListMapper.updateTargetStatus(targetId,"4");
       }
    }

    /**
     * 按年获取gmv的历史数据 传入的参数为年份列表
     * @return
     */
    private List<TgtReference> getGmvHistoryByYear(List<String> periodList,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            W_DATE.YEAR PERIOD_NAME,\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$\n" +
                "       GROUP BY W_DATE.YEAR";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();

        if(periodList.size()==1)
        {
            periodInfo.append(" AND W_DATE.YEAR="+periodList.get(0));
        }else
        {
            periodInfo.append(" AND W_DATE.YEAR IN ("+ Joiner.on(",").skipNulls().join(periodList)+")");
        }

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 按月获取gmv的历史数据 传入的参数为月份列表
     * @return
     */
    private List<TgtReference> getGmvHistoryByMonth(List<String> periodList,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            W_DATE.MONTH_SHORT_NAME PERIOD_NAME,\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$\n" +
                "       GROUP BY W_DATE.MONTH_SHORT_NAME";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();


        if(periodList.size()==1)
        {
            periodInfo.append(" AND W_DATE.MONTH="+periodList.get(0));
        }else
        {
            periodInfo.append(" AND W_DATE.MONTH IN ("+Joiner.on(",").skipNulls().join(periodList)+")");
        }

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 按月获取gmv的历史数据 传入的参数为年ID
     * @return
     */
    private List<TgtReference> getGmvHistoryByMonth(String year,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            W_DATE.MONTH_SHORT_NAME PERIOD_NAME,\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$\n" +
                "       GROUP BY W_DATE.MONTH_SHORT_NAME";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer(" AND W_DATE.YEAR="+year);

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 按天获取gmv的情况 传入的参数为开始日期 结束日期
     * @return
     */
    private List<TgtReference> getGmvHistoryByDay(String startDay,String endDay,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            W_DATE.DAY_SHORT_NAME PERIOD_NAME,\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$\n" +
                "       GROUP BY W_DATE.DAY_SHORT_NAME";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();
        periodInfo.append(" AND W_DATE.ROW_WID>="+startDay+" AND W_DATE.ROW_WID<="+endDay);

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 按太难获取gmv的实际数据 传入的参数为month 月份ID
     * @return
     */
    private List<TgtReference> getGmvHistoryByDay(String month,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            W_DATE.DAY_SHORT_NAME PERIOD_NAME,\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$\n" +
                "       GROUP BY W_DATE.DAY_SHORT_NAME";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer(" AND W_DATE.MONTH="+month);

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 获取从YYYYMMDD到YYYYMMDD之间的gmv合计值
     * @return
     */
    private TgtReference getGmvHistoryByPeriodDay(String startDay,String endDay,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();
        periodInfo.append(" AND W_DATE.ROW_WID>="+startDay+" AND W_DATE.ROW_WID<="+endDay);

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeriodDay(stringTemplate.render());

    }


    private TemplateResult buildWhereAndJoinInfo(Map<String, String> dimInfo)
    {
        Joiner joiner = Joiner.on(",").skipNulls();
        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        dimInfo.forEach((k,v)->{
            //根据key获取关联信息
            DimJoinVO dimJoinVO= KpiCacheManager.getInstance().getDimJoinList().get("W_ORDERS",k);

            joins.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());

            //where条件
            List<String> values= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(v);
            //字符串类型
            if("STRING".equals(dimJoinVO.getDimWhereType()))
            {
                if(values.size()==1)
                {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append("='").append(values.get(0)).append("'");
                }else
                {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values.stream().map(a->"'"+a+"'").toArray())).append(")");
                }
            }else if("NUMBER".equals(dimJoinVO.getDimWhereType())) {
                //数字类型
                if (values.size() == 1) {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append("=").append(values.get(0));
                } else {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values)).append(")");
                }
            }

        });

        TemplateResult result=new TemplateResult();
        result.setFilterInfo(filters.toString());
        result.setJoinInfo(joins.toString());

        return result;
    }
}
