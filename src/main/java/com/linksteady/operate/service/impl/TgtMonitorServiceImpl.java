package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.TgtDismantMapper;
import com.linksteady.operate.domain.TgtDismant;
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

    @Autowired
    private TgtDismantMapper tgtDismantMapper;

    @Override
    public List<Echart> getCharts(String targetId, String periodType, String dt) {
        Echart echart1 = getActualTgtVal(targetId, getPeriodDate(periodType, dt, false));
        Echart echart2 = getCurrLastVal(targetId, getPeriodDate(periodType, dt, true));
        Echart echart3 = getValRate(targetId, getPeriodDate(periodType, dt, true));
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
    private Echart getActualTgtVal(String targetId, List<String> xdata) {
        List<TgtDismant> dataList = tgtDismantMapper.findByTgtId(Long.valueOf(targetId));
        dataList.stream().forEach(x-> {
            x.setPeriodDate(x.getPeriodDate().replaceAll("-", ""));
        });
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("目标值", "实际值"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "目标值");
        Map<String, Double> datas = dataList.stream().collect(Collectors.toMap(TgtDismant::getPeriodDate, TgtDismant::getTgtVal));
        List<Double> data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "bar");
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "实际值");
        datas = dataList.stream().collect(Collectors.toMap(TgtDismant::getPeriodDate, TgtDismant::getActualVal));
        data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "bar");
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    /**
     *
     * @param targetId
     * @return
     */
    private Echart getCurrLastVal(String targetId, List<String> xdata) {
        DecimalFormat df = new DecimalFormat("#.##");
        List<TgtDismant> dataList = tgtDismantMapper.findByTgtId(Long.valueOf(targetId));
        dataList.stream().forEach(x-> {
            x.setPeriodDate(x.getPeriodDate().replaceAll("-", ""));
        });
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("本周期指标值", "去年同期指标值", "本周期平均指标值", "去年同期平均指标值"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "本周期指标值");
        Map<String, Double> datas = dataList.stream().collect(Collectors.toMap(TgtDismant::getPeriodDate, TgtDismant::getActualVal));
        List<Double> current = fixData(datas, xdata);
        map.put("data", current);
        map.put("type", "line");
        list.add(map);
        DoubleSummaryStatistics statistics = current.stream().collect(Collectors.summarizingDouble(x->x));
        String avgCurrent = df.format(statistics.getSum()/current.stream().count());
        List<String> avgCurrentList = current.stream().map(x->avgCurrent).collect(Collectors.toList());

        map = Maps.newHashMap();
        map.put("name", "本周期平均指标值");
        map.put("data", avgCurrentList);
        map.put("type", "line");
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "去年同期指标值");
        map.put("type", "line");
        dataList.stream().forEach(x-> {
            x.setActualValLast(x.getActualValLast() == null ? 0D:x.getActualValLast());
        });
        datas = dataList.stream().collect(Collectors.toMap(TgtDismant::getPeriodDate, TgtDismant::getActualValLast));
        List<Double> last = fixData(datas, xdata);
        map.put("data", last);
        list.add(map);


        DoubleSummaryStatistics sts = last.stream().collect(Collectors.summarizingDouble(x->x));
        String avgLast = df.format(sts.getSum()/last.stream().count());
        List<String> avgLastList = last.stream().map(x->avgLast).collect(Collectors.toList());
        map = Maps.newHashMap();
        map.put("name", "去年同期平均指标值");
        map.put("data", avgLastList);
        map.put("type", "line");
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    private Echart getValRate(String targetId, List<String> xdata) {
        List<TgtDismant> dataList = tgtDismantMapper.findByTgtId(Long.valueOf(targetId));
        dataList.stream().forEach(x-> {
            x.setPeriodDate(x.getPeriodDate().replaceAll("-", ""));
        });
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("指标值", "环比增长率"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "指标值");
        Map<String, Double> datas = dataList.stream().collect(Collectors.toMap(TgtDismant::getPeriodDate, TgtDismant::getActualVal));
        List<Double> data = fixData(datas, xdata);
        map.put("data", data);
        map.put("type", "bar");
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "环比增长率");
        dataList.stream().forEach(x-> {
            x.setGrowthRate(x.getGrowthRate() == null ? 0D:x.getGrowthRate());
        });
        datas = dataList.stream().collect(Collectors.toMap(TgtDismant::getPeriodDate, TgtDismant::getGrowthRate));
        data = fixData(datas, xdata);
        List<String> newData = data.stream().map(x->{
            if(x == 0D) {
                return "";
            }else {
                return String.valueOf(x);
            }
        }).collect(Collectors.toList());
        map.put("data", newData);
        map.put("type", "line");
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        System.out.println(now.getMonthValue());
    }

    /**
     * 获取周期内的时间
     * @param periodType
     * @param dt
     * @param flag 如果true，则截止当前日期的前一个周期
     * @return
     */
    private static List<String> getPeriodDate(String periodType, String dt, Boolean flag) {
        LocalDate now = LocalDate.now();
        List<String> dateList = Lists.newArrayList();
        if("year".equals(periodType)) {
            for(int i=1; i<=12;i++) {
                if(flag) {
                    if(Integer.valueOf(dt) == now.getYear()) {
                        if(i<now.getMonthValue()) {
                            String t = i < 10 ? "0" + i:String.valueOf(i);
                            dateList.add(dt+t);
                        }
                    }
                }
                if(!flag) {
                    String t = i < 10 ? "0" + i:String.valueOf(i);
                    dateList.add(dt+t);
                }
            }
        }
        if("month".equals(periodType)) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dt + "-01", df);
            LocalDate firstDay = LocalDate.of(localDate.getYear(),localDate.getMonth(),1);
            LocalDate lastDay =localDate.with(TemporalAdjusters.lastDayOfMonth());

            if(flag) {
                LocalDate lstDay = now.plusDays(-1);
                lastDay = lastDay.isAfter(now) ? lstDay:lastDay;

                if(firstDay.isBefore(now)) {
                    while(firstDay.isBefore(lastDay)) {
                        dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        firstDay = firstDay.plusDays(1);
                    }
                    dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                }
            }else {
                while(firstDay.isBefore(lastDay)) {
                    dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    firstDay = firstDay.plusDays(1);
                }
                dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
        }
        if("day".equals(periodType)) {
            String[] dateArray = dt.split("~");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDt = LocalDate.parse(dateArray[0], df);
            LocalDate endDt = LocalDate.parse(dateArray[1], df);

            if(flag) {
                LocalDate lstDay = now.plusDays(-1);
                endDt = endDt.isAfter(now) ? lstDay:endDt;
                if(startDt.isBefore(now)) {
                    while(startDt.isBefore(endDt)) {
                        dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        startDt = startDt.plusDays(1);
                    }
                    dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                }
            }else {
                while(startDt.isBefore(endDt)) {
                    dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    startDt = startDt.plusDays(1);
                }
                dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
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
