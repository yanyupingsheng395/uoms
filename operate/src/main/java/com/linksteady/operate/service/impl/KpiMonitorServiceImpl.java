package com.linksteady.operate.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.KpiMonitorMapper;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.vo.Echart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cao
 */
@Slf4j
@Service
public class KpiMonitorServiceImpl implements KpiMonitorService {

    @Autowired
    private KpiMonitorMapper kpiMonitorMapper;

    /**
     * @param startDt
     *
     * @param endDt
     * @return
     */
    @Override
    public Echart getGMV(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(spuId)) {
            list = kpiMonitorMapper.getGMVBySpu(startDt, endDt, spuId);
        }else {
            list = kpiMonitorMapper.getGMV(startDt, endDt);
        }

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
        List<Map<String, Object>> list  = Lists.newArrayList();
        if(StringUtils.isNotBlank(spuId)) {
            list = kpiMonitorMapper.getTradeUserBySpu(startDt, endDt, spuId);
        }else {
            list = kpiMonitorMapper.getTradeUser(startDt, endDt);
        }
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
        List<Map<String, Object>> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(spuId)) {
            list = kpiMonitorMapper.getAvgCsPriceBySpu(startDt, endDt, spuId);
        }else {
            list = kpiMonitorMapper.getAvgCsPrice(startDt, endDt);
        }
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

    @Override
    public Map<String, Object> getTotalGmv(String startDt, String endDt) {
        String lastYearStartDt = DateUtil.getLastYear(startDt);
        String lastYearEndDt = DateUtil.getLastYear(endDt);

        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");

        lastYearStartDt = lastYearStartDt.replaceAll("-", "");
        lastYearEndDt = lastYearEndDt.replaceAll("-", "");

        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat(",###");
        Double d1 = kpiMonitorMapper.getTotalGmv(startDt, endDt);

        Double d2 = kpiMonitorMapper.getTotalGmv(lastYearStartDt, lastYearEndDt);
        result.put("gmv", d1 == null ? df.format(0) : df.format(d1));
        df = new DecimalFormat("#.00%");
        result.put("yny", d2 == null ? df.format(1) : df.format((d1-d2)/d2));
        return result;
    }



    @Override
    public Map<String, Object> getTotalTradeUser(String startDt, String endDt) {
        String lastYearStartDt = DateUtil.getLastYear(startDt);
        String lastYearEndDt = DateUtil.getLastYear(endDt);

        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");

        lastYearStartDt = lastYearStartDt.replaceAll("-", "");
        lastYearEndDt = lastYearEndDt.replaceAll("-", "");

        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat(",###");
        Double d1 = kpiMonitorMapper.getTotalTradeUser(startDt, endDt);

        Double d2 = kpiMonitorMapper.getTotalTradeUser(lastYearStartDt, lastYearEndDt);
        result.put("tradeUser", d1 == null ? df.format(0) : df.format(d1));
        df = new DecimalFormat("#.00%");
        result.put("yny", d2 == null ? df.format(1) : df.format((d1-d2)/d2));
        return result;
    }

    @Override
    public Map<String, Object> getTotalAvgPrice(String startDt, String endDt) {
        String lastYearStartDt = DateUtil.getLastYear(startDt);
        String lastYearEndDt = DateUtil.getLastYear(endDt);

        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");

        lastYearStartDt = lastYearStartDt.replaceAll("-", "");
        lastYearEndDt = lastYearEndDt.replaceAll("-", "");

        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat(",###");
        Double d1 = kpiMonitorMapper.getTotalAvgPrice(startDt, endDt);

        Double d2 = kpiMonitorMapper.getTotalAvgPrice(lastYearStartDt, lastYearEndDt);
        result.put("avgPrice", d1 == null ? df.format(0) : df.format(d1));
        df = new DecimalFormat("#.00%");
        result.put("yny", d2 == null ? df.format(1) : df.format((d1-d2)/d2));
        return result;
    }

    @Override
    public Echart getOrderAvgPrice(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgOrderPrice(startDt, endDt);
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
        legendData.add("首购平均订单价");
        legendData.add("非首购平均订单价");
        legendData.add("整体平均订单价");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均订单价");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均订单价");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均订单价");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均订单价");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgOrderQuantity(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgOrderQuantity(startDt, endDt);
        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_ORDER_QTT")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_ORDER_QTT")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("ORDER_QTT")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均订单数");
        legendData.add("非首购平均订单数");
        legendData.add("整体平均订单数");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均订单数");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均订单数");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均订单数");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均订单数");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgPiecePrice(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgPiecePrice(startDt, endDt);
        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), t.get("FP_UP")==null?0D:((BigDecimal)t.get("FP_UP")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), t.get("RP_UP")==null?0D:((BigDecimal)t.get("RP_UP")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), t.get("TOTAL_UP")==null?0D:((BigDecimal)t.get("TOTAL_UP")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均件单价");
        legendData.add("非首购平均件单价");
        legendData.add("整体平均件单价");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均件单价");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均件单价");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均件单价");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均件单价");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgJoinRate(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgJoinRate(startDt, endDt);
        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), t.get("FP_JOIN_RATE")==null?0D:((BigDecimal)t.get("FP_JOIN_RATE")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), t.get("RP_JOIN_RATE")==null?0D:((BigDecimal)t.get("RP_JOIN_RATE")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), t.get("JOIN_RATE")==null?0D:((BigDecimal)t.get("JOIN_RATE")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均连带率");
        legendData.add("非首购平均连带率");
        legendData.add("整体平均连带率");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均连带率");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均连带率");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均连带率");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均连带率");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }
}