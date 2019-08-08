package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.service.DailyDetailService;
import com.linksteady.operate.vo.Echart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class DailyDetailServiceImpl implements DailyDetailService {

    private final String[] RETENTION_CODE = {"target01", "target02", "target03"};


    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Override
    public Echart getTargetType(String headId) {
        Echart echart = new Echart();
        List<String> xAxisData = Lists.newLinkedList();
        List<String> yAxisData1;
        List<String> yAxisData2;
        List<Map<String, Object>> seriesData = Lists.newArrayList();

        List<Map<String, Object>> allData = dailyDetailMapper.getTargetType(headId);
        List<Map<String, Object>> checkedData = dailyDetailMapper.getTargetTypeOfCheck(headId);

        List<String> legend = Lists.newLinkedList();
        legend.add("默认推荐用户");
        legend.add("实际推荐用户");

        xAxisData.add("提升");
        xAxisData.add("留存");
        xAxisData.add("挽回");

        List<String> cols = Arrays.asList(RETENTION_CODE);
        yAxisData1 = convertData(getYAxisData(allData), cols);
        yAxisData2 = convertData(getYAxisData(checkedData), cols);

        Map<String, Object> series1 = Maps.newHashMap();
        series1.put("name", "默认推荐用户");
        series1.put("type", "bar");
        series1.put("data", yAxisData1);

        Map<String, Object> series2 = Maps.newHashMap();
        series2.put("name", "实际推荐用户");
        series2.put("type", "bar");
        series2.put("data", yAxisData2);

        seriesData.add(series1);
        seriesData.add(series2);

        echart.setSeriesData(seriesData);
        echart.setxAxisData(xAxisData);
        echart.setLegendData(legend);
        echart.setxAxisName("目标分类");
        echart.setyAxisName("占比(%)");
        return echart;
    }

    private Map<String, Double> getYAxisData(List<Map<String, Object>> dataList) {
        int total = dataList.stream().map(x-> x.get("COUNT")).mapToInt(x->Integer.valueOf(x.toString())).sum();
        List<String> k = dataList.stream().map(x->x.get("TYPE").toString()).collect(Collectors.toList());
        List<Double> v = dataList.stream().map(x-> Double.valueOf(x.get("COUNT").toString())/total).collect(Collectors.toList());

        Map<String, Double> result = Maps.newHashMap();
        for(int i=0; i<k.size();i++) {
            result.put(k.get(i), v.get(i));
        }
        return result;
    }

    public List<String> convertData(Map<String, Double> map, List<String> cols) {
        DecimalFormat df = new DecimalFormat(".##");
        List<String> result = Lists.newLinkedList();
        for(String c:cols) {
            Double v = map.get(c);
            if(v != null) {
                result.add(df.format(v * 100D));
            }else {
                result.add("0%");
            }
        }
        return result;
    }


    @Override
    public Echart getUrgency(String headId) {
        Echart echart = new Echart();
        List<String> xAxisData;
        List<String> yAxisData1;
        List<String> yAxisData2;
        List<Map<String, Object>> seriesData = Lists.newArrayList();

        List<Map<String, Object>> allData = dailyDetailMapper.getUrgency(headId);
        List<Map<String, Object>> checkedData = dailyDetailMapper.getUrgencyOfCheck(headId);

        List<String> legend = Lists.newLinkedList();
        legend.add("默认推荐用户");
        legend.add("实际推荐用户");

        String[] x = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        xAxisData = Arrays.asList(x);

        yAxisData1 = convertData(getYAxisData(allData), xAxisData);
        yAxisData2 = convertData(getYAxisData(checkedData), xAxisData);

        Map<String, Object> series1 = Maps.newHashMap();
        series1.put("name", "默认推荐用户");
        series1.put("type", "bar");
        series1.put("data", yAxisData1.toArray());

        Map<String, Object> series2 = Maps.newHashMap();
        series2.put("name", "实际推荐用户");
        series2.put("type", "bar");
        series2.put("data", yAxisData2.toArray());

        seriesData.add(series1);
        seriesData.add(series2);

        echart.setSeriesData(seriesData);
        echart.setxAxisData(xAxisData);
        echart.setLegendData(legend);
        echart.setxAxisName("紧迫度");
        echart.setyAxisName("占比(%)");
        return echart;
    }

    @Override
    public List<DailyDetail> getPageList(int start, int end, String headId, String groupIds) {
        List<String> groupIdList = Lists.newArrayList();
        if(groupIds != null) {
            groupIdList = Arrays.asList(groupIds.split(","));
        }
        return dailyDetailMapper.getPageList(start, end, headId, groupIdList);
    }

    @Override
    public int getDataCount(String headId, String groupIds) {
        List<String> groupIdList = Lists.newArrayList();
        if(groupIds != null) {
            groupIdList = Arrays.asList(groupIds.split(","));
        }
        return dailyDetailMapper.getDataCount(headId, groupIdList);
    }
}
