package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.KpiMonitorMapper;
import com.linksteady.operate.dao.OpMapper;
import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.service.OpService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KpiMonitorServiceImpl implements KpiMonitorService {

    @Autowired
    private KpiMonitorMapper kpiMonitorMapper;


    @Override
    public List<WeekInfo> getWeekList(String start, String end) {
        return kpiMonitorMapper.getWeekList(Integer.parseInt(start.replace("-","")),Integer.parseInt(end.replace("-","")));
    }

    /**
     *
     * @param startDt
     * @param endDt
     * @return
     */
    @Override
    public Echart getGMV(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getGMV(startDt, endDt, spuId);

        Map<String, Double> fp = new HashMap<>();
        Map<String, Double> rp = new HashMap<>();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_GMV")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_GMV")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购GMV");
        legendData.add("非首购GMV");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("GMV值（元）");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        tmp.put("name", "首购GMV");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购GMV");
        tmp.put("data", rpList);
        seriesData.add(tmp);

        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getTradeUser(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getTradeUser(startDt, endDt, spuId);

        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_CNT")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_CNT")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("TOTAL_CNT")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购用户数");
        legendData.add("非首购用户数");
        legendData.add("整体用户数");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("交易用户数");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购用户数");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购用户数");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体用户数");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgCsPrice(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgCsPrice(startDt, endDt, spuId);

        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_PRICE")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_PRICE")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("PRICE")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均客单价");
        legendData.add("非首购平均客单价");
        legendData.add("整体平均客单价");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均客单价");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均客单价");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均客单价");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均客单价");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    private List<Double> fixData(Map<String,Double> datas,List<String> periodList) {
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
