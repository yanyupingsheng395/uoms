package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.TgtMonitorMapper;
import com.linksteady.operate.domain.TgtMonitor;
import com.linksteady.operate.service.TgtMonitorService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-05-24
 */
@Service
public class TgtMonitorServiceImpl implements TgtMonitorService {

    private static final String CHART_AC_TGT = "acc_tgt";
    private static final String CHART_CURR_LAST = "curr_last";
    private static final String CHART_VAL_RATE = "val_rate";

    @Autowired
    private TgtMonitorMapper tgtMonitorMapper;

    @Override
    public List<Echart> getCharts(String targetId, String periodType, String dt) {
        List<String> xdata = getPeriodDate(periodType, dt);
        Echart echart1 = getActualTgtVal(targetId, CHART_AC_TGT, xdata);
        Echart echart2 = getCurrLastVal(targetId, CHART_CURR_LAST, xdata);
        Echart echart3 = getValRate(targetId, CHART_VAL_RATE, xdata);
        List<Echart> list = Lists.newArrayList();
        list.add(echart1);
        list.add(echart2);
        list.add(echart3);
        return list;
    }

    /**
     * 获取周期内目标值和实际值
     * @return
     */
    private Echart getActualTgtVal(String targetId, String type, List<String> xdata) {
        List<TgtMonitor> dataList = tgtMonitorMapper.getDataByTgtId(targetId, type);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("目标值", "实际值"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "目标值");
        Map<String, Double> datas = dataList.stream().collect(Collectors.toMap(TgtMonitor::getPeriodDate, TgtMonitor::getTgtVal));
        List<Double> data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "bar");
        map.put("xAxisIndex", 0);
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "实际值");
        datas = dataList.stream().collect(Collectors.toMap(TgtMonitor::getPeriodDate, TgtMonitor::getActualVal));
        data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "bar");
        map.put("xAxisIndex", 1);
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    /**
     *
     * @param targetId
     * @param type
     * @return
     */
    private Echart getCurrLastVal(String targetId, String type, List<String> xdata) {
        DecimalFormat df = new DecimalFormat("#.##");
        List<TgtMonitor> dataList = tgtMonitorMapper.getDataByTgtId(targetId, type);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("本年指标值", "去年指标值", "本年平均指标值", "去年平均指标值"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "本年指标值");
        Map<String, Double> datas = dataList.stream().collect(Collectors.toMap(TgtMonitor::getPeriodDate, TgtMonitor::getActualVal));
        List<Double> current = fixData(datas, xdata);
        map.put("data", current);
        map.put("type", "line");
        list.add(map);
        DoubleSummaryStatistics statistics = current.stream().collect(Collectors.summarizingDouble(x->x));
        String avgCurrent = df.format(statistics.getSum()/current.stream().count());
        List<String> avgCurrentList = current.stream().map(x->avgCurrent).collect(Collectors.toList());

        map = Maps.newHashMap();
        map.put("name", "本年平均指标值");
        map.put("data", avgCurrentList);
        map.put("type", "line");
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "去年指标值");
        map.put("type", "line");
        datas = dataList.stream().collect(Collectors.toMap(TgtMonitor::getPeriodDate, TgtMonitor::getActualValLast));
        List<Double> last = fixData(datas, xdata);
        map.put("data", last);
        list.add(map);


        DoubleSummaryStatistics sts = last.stream().collect(Collectors.summarizingDouble(x->x));
        String avgLast = df.format(sts.getSum()/last.stream().count());
        List<String> avgLastList = last.stream().map(x->avgLast).collect(Collectors.toList());
        map = Maps.newHashMap();
        map.put("name", "去年平均指标值");
        map.put("data", avgLastList);
        map.put("type", "line");
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    private Echart getValRate(String targetId, String type, List<String> xdata) {
        List<TgtMonitor> dataList = tgtMonitorMapper.getDataByTgtId(targetId, type);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("指标值", "环比增长率"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "指标值");
        Map<String, Double> datas = dataList.stream().collect(Collectors.toMap(TgtMonitor::getPeriodDate, TgtMonitor::getActualVal));
        List<Double> data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "bar");
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "环比增长率");
        datas = dataList.stream().collect(Collectors.toMap(TgtMonitor::getPeriodDate, TgtMonitor::getGrowthRate));
        data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "line");
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    /**
     * 获取周期内的时间
     * @return
     */
    private static List<String> getPeriodDate(String periodType, String dt) {
        List<String> dateList = Lists.newArrayList();
        if("year".equals(periodType)) {
            for(int i=1; i<=12;i++) {
                String t = i < 10 ? "0" + i:String.valueOf(i);
                dateList.add(dt+t);
            }
        }
        if("month".equals(periodType)) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dt + "-01", df);
            LocalDate firstDay = LocalDate.of(localDate.getYear(),localDate.getMonth(),1);
            LocalDate lastDay =localDate.with(TemporalAdjusters.lastDayOfMonth());
            while(firstDay.isBefore(lastDay)) {
                dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                firstDay = firstDay.plusDays(1);
            }
            dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        if("day".equals(periodType)) {
            String[] dateArray = dt.split("~");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDt = LocalDate.parse(dateArray[0], df);
            LocalDate endDt = LocalDate.parse(dateArray[1], df);
            while(startDt.isBefore(endDt)) {
                dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                startDt = startDt.plusDays(1);
            }
            dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        return dateList;
    }

    private List<Double> fixData(Map<String,Double> datas,List<String> periodList)
    {
        return periodList.stream().map(s->{
            if(null==datas.get(s)||"".equals(datas.get(s)))
            {
                return 0d;
            }else
            {
                return datas.get(s);
            }
        }).collect(Collectors.toList());
    }
}
