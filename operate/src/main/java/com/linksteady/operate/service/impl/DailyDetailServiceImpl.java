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

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class DailyDetailServiceImpl implements DailyDetailService {

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

        yAxisData1 = getYAxisData(allData);
        yAxisData2 = getYAxisData(checkedData);

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
        echart.setxAxisName("目标分类");
        echart.setyAxisName("百分比");
        return echart;
    }

    private List<String> getYAxisData(List<Map<String, Object>> dataList) {
        DecimalFormat df = new DecimalFormat("#.##%");
        List<String> yAxisData = Lists.newLinkedList();
        int total = dataList.stream().map(x-> x.get("count")).mapToInt(x->Integer.valueOf(x.toString())).sum();
        dataList.stream().forEach(x-> {
            yAxisData.add( df.format(Integer.valueOf(x.get("count").toString())/total)); });
        return yAxisData;
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

        yAxisData1 = getYAxisData(allData);
        yAxisData2 = getYAxisData(checkedData);

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
        echart.setyAxisName("百分比");
        return echart;
    }

    @Override
    public List<DailyDetail> getPageList(int start, int end, String headId, String groupId) {

        return dailyDetailMapper.getPageList(start, end, headId, groupId);
    }

    @Override
    public int getDataCount(String headId, String groupId) {
        return dailyDetailMapper.getDataCount(headId, groupId);
    }
}
