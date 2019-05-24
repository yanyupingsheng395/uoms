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
    public List<Echart> getCharts(String targetId) {
        Echart echart1 = getActualTgtVal(targetId, CHART_AC_TGT);
        Echart echart2 = getCurrLastVal(targetId, CHART_CURR_LAST);
        Echart echart3 = getValRate(targetId, CHART_VAL_RATE);
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
    private Echart getActualTgtVal(String targetId, String type) {
        List<TgtMonitor> dataList = tgtMonitorMapper.getDataByTgtId(targetId, type);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        List<String> xdata = dataList.stream().map(x->x.getPeriodDate()).collect(Collectors.toList());
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("目标值", "实际值"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "目标值");
        map.put("data", dataList.stream().map(x->x.getTgtVal()).collect(Collectors.toList()));
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "实际值");
        map.put("data", dataList.stream().map(x->x.getActualVal()).collect(Collectors.toList()));
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
    private Echart getCurrLastVal(String targetId, String type) {
        DecimalFormat df = new DecimalFormat("#.##");
        List<TgtMonitor> dataList = tgtMonitorMapper.getDataByTgtId(targetId, type);
        Echart echart = new Echart();
        echart.setxAxisName("指标值");
        echart.setyAxisName("日期");
        List<String> xdata = dataList.stream().map(x->x.getPeriodDate()).collect(Collectors.toList());
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("本年指标值", "去年指标值", "本年平均指标值", "去年平均指标值"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "本年指标值");
        List<Double> current = dataList.stream().map(x->x.getActualVal()).collect(Collectors.toList());
        map.put("data", current);
        list.add(map);
        DoubleSummaryStatistics statistics = current.stream().collect(Collectors.summarizingDouble(x->x));
        String avgCurrent = df.format(statistics.getSum()/current.stream().count());
        List<String> avgCurrentList = current.stream().map(x->avgCurrent).collect(Collectors.toList());

        map = Maps.newHashMap();
        map.put("name", "本年平均指标值");
        map.put("data", avgCurrentList);

        map = Maps.newHashMap();
        map.put("name", "去年指标值");
        List<Double> last = dataList.stream().map(x->x.getActualValLast()).collect(Collectors.toList());
        map.put("data", last);
        list.add(map);


        DoubleSummaryStatistics sts = last.stream().collect(Collectors.summarizingDouble(x->x));
        String avgLast = df.format(sts.getSum()/last.stream().count());
        List<String> avgLastList = last.stream().map(x->avgLast).collect(Collectors.toList());
        map = Maps.newHashMap();
        map.put("name", "去年平均指标值");
        map.put("data", avgLastList);
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }

    private Echart getValRate(String targetId, String type) {
        List<TgtMonitor> dataList = tgtMonitorMapper.getDataByTgtId(targetId, type);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("指标值");
        List<String> xdata = dataList.stream().map(x->x.getPeriodDate()).collect(Collectors.toList());
        echart.setxAxisData(xdata);
        echart.setLegendData(Arrays.asList("指标值", "环比增长率"));
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "指标值");
        map.put("data", dataList.stream().map(x->x.getActualVal()).collect(Collectors.toList()));
        list.add(map);
        map = Maps.newHashMap();
        map.put("name", "环比增长率");
        map.put("data", dataList.stream().map(x->x.getGrowthRate()).collect(Collectors.toList()));
        list.add(map);
        echart.setSeriesData(list);
        return echart;
    }
}
