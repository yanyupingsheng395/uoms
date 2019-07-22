package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.SpuLifeCycleMapper;
import com.linksteady.operate.service.SpuLifeCycleService;
import com.linksteady.operate.vo.Echart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-05-10
 */
@Service
public class SpuLifeCycleServiceImpl implements SpuLifeCycleService {

    @Autowired
    private SpuLifeCycleMapper spuLifeCycleMapper;

    private static final String STAGE_PERIOD_0 = "puchtimes_gap_repurch"; // 复购
    private static final String STAGE_PERIOD_1 = "puchtimes_gap_loyal"; // 忠诚
    private static final String STAGE_PERIOD_2 = "puchtimes_gap_decline"; // 衰退

    @Override
    public String getSampleDate(String spuId) {
        return spuLifeCycleMapper.getSampleDate(spuId);
    }

    @Override
    public Echart retentionPurchaseTimes(String spuId) {
        Echart echart = new Echart();
        List<Map<String, Object>> data = spuLifeCycleMapper.retentionPurchaseTimes(spuId);
        Map<String, Object> points = Maps.newHashMap();
        Map<String, Object> xpoint =  data.stream().filter(x->x.get("IF_UNUM_LIMIT").equals("Y")).findFirst().orElse(null);
        List<String> retention = Lists.newArrayList();
        List<String> purchTimes = Lists.newArrayList();
        data.stream().forEach(t->{
            retention.add(String.valueOf(t.get("RETENTION")));
            purchTimes.add(String.valueOf(t.get("PURCH_TIMES")));
        });
        echart.setxAxisName("购买次数");
        echart.setyAxisName("留存率（%）");

        echart.setxAxisData(purchTimes);
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("data", retention);

        if(xpoint != null) {
            String p = String.valueOf(xpoint.get("PURCH_TIMES"));
            int index = purchTimes.stream().filter(y->y.equals(p)).map(x->purchTimes.indexOf(x)).findFirst().get();
            points.put("xAxis", index);
            points.put("yAxis", retention.get(index));
            Map<String, Object> pointData = Maps.newHashMap();
            pointData.put("data", Arrays.asList(points));
            tmp.put("markPoint", pointData);
        }
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }

    /**
     * puchtimes_gap_repurch  --复购 购买次数随购买间隔的变化
     * puchtimes_gap_loyal  --忠诚 购买次数随购买间隔的变化
     * puchtimes_gap_decline  --衰退 购买次数随购买间隔的变化
     * @param spuId
     */
    @Override
    public Echart getPurchDateChart(String spuId, String type) {
        List<Map<String, Object>> data = spuLifeCycleMapper.getLcDate(spuId, type);
        List<String> xdata = data.stream().map(x->x.get("dt_period").toString()).collect(Collectors.toList());
        List<String> dataList = data.stream().map(x->x.get("purch_times").toString()).collect(Collectors.toList());
        Echart echart = new Echart();
        echart.setxAxisData(xdata);
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("data", dataList);
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }

    @Override
    public Echart getUnitPriceChart(String spuId) {
        Echart echart = new Echart();
        List<Map<String, Object>> data  = spuLifeCycleMapper.getUnitPriceChart(spuId);
        List<String> unitPrice = Lists.newArrayList();
        List<String> purchTimes = Lists.newArrayList();
        data.stream().forEach(t->{
            unitPrice.add(String.valueOf(t.get("UNIT_PRICE")));
            purchTimes.add(String.valueOf(t.get("PURCH_TIMES")));
        });
        echart.setxAxisName("购买次数");
        echart.setyAxisName("件单价（元）");

        echart.setxAxisData(purchTimes);
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("data", unitPrice);
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }

    /**
     * 时间间隔-购买次数
     * @param spuId
     * @return
     */
    @Override
    public Echart getDtPeriodChart(String spuId) {
        Echart echart = new Echart();
        List<Map<String, Object>> data  = spuLifeCycleMapper.getDtPeriodChart(spuId);
        List<String> periodList = Lists.newArrayList();
        List<String> purchTimes = Lists.newArrayList();
        data.stream().forEach(t->{
            periodList.add(String.valueOf(t.get("DT_PERIOD")));
            purchTimes.add(String.valueOf(t.get("PURCH_TIMES")));
        });
        echart.setxAxisName("购买次数");
        echart.setyAxisName("时间间隔（天）");

        echart.setxAxisData(purchTimes);
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("data", periodList);
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }

    /**
     * 连带率-购买次数
     * @param spuId
     * @return
     */
    @Override
    public Echart getRateChart(String spuId) {
        Echart echart = new Echart();
        List<Map<String, Object>> data  = spuLifeCycleMapper.getRateChart(spuId);
        List<String> rateList = Lists.newArrayList();
        List<String> purchTimes = Lists.newArrayList();
        data.stream().forEach(t->{
            rateList.add(String.valueOf(t.get("JOINT_RATE")));
            purchTimes.add(String.valueOf(t.get("PURCH_TIMES")));
        });
        echart.setxAxisName("购买次数");
        echart.setyAxisName("连带率");

        echart.setxAxisData(purchTimes);
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("data", rateList);
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }

    @Override
    public Echart getCateChart(String spuId) {
        Echart echart = new Echart();
        List<Map<String, Object>> data  = spuLifeCycleMapper.getCateChart(spuId);
        List<String> cateList = Lists.newArrayList();
        List<String> purchTimes = Lists.newArrayList();
        data.stream().forEach(t->{
            cateList.add(String.valueOf(t.get("CATE_NUM")));
            purchTimes.add(String.valueOf(t.get("PURCH_TIMES")));
        });
        echart.setxAxisName("购买次数");
        echart.setyAxisName("品类种数");

        echart.setxAxisData(purchTimes);
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("data", cateList);
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }

    @Override
    public Echart getUserCountChart(String spuId) {
        List<Map<String, Object>> data = spuLifeCycleMapper.getLifeCycleKpi(spuId);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("用户数");
        List<String> legendData = Lists.newArrayList();
        legendData.add("新客期");
        legendData.add("成长期");
        legendData.add("成熟期");
        legendData.add("衰退期");
        echart.setLegendData(legendData);

        List<String> xdata = data.stream().map(x-> String.valueOf(x.get("COMPUTE_DT"))).distinct().collect(Collectors.toList());

        List<Map<String, Object>> series = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("name", "新客期");
        tmp.put("data", getResult(data, "USER_COUNT", "0"));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成长期");
        tmp.put("data", getResult(data, "USER_COUNT", "1"));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成熟期");
        tmp.put("data", getResult(data, "USER_COUNT", "2"));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "衰退期");
        tmp.put("data", getResult(data, "USER_COUNT", "3"));
        series.add(tmp);
        echart.setSeriesData(series);
        echart.setxAxisData(xdata);
        return echart;
    }

    private List<Double> getResult(List<Map<String, Object>> data, String filed, String type) {
        return data.stream().filter(y-> StringUtils.isNotBlank(String.valueOf(y.get("LIFECYCLE_TYPE"))) && y.get("LIFECYCLE_TYPE").equals(type)).map(x->Double.valueOf(x.get(filed).toString())).collect(Collectors.toList());
    }

    @Override
    public Echart getSaleVolumeChart(String spuId) {
        List<Map<String, Object>> data = spuLifeCycleMapper.getLifeCycleKpi(spuId);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("销售额（元）");
        List<String> legendData = Lists.newArrayList();
        legendData.add("新客期");
        legendData.add("成长期");
        legendData.add("成熟期");
        legendData.add("衰退期");
        echart.setLegendData(legendData);

        List<String> xdata = data.stream().map(x-> String.valueOf(x.get("COMPUTE_DT"))).distinct().collect(Collectors.toList());

        List<Map<String, Object>> series = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("name", "新客期");
        tmp.put("data", getResult(data, "GMV_TOTAL", "0"));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成长期");
        tmp.put("data", getResult(data, "GMV_TOTAL", "1"));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成熟期");
        tmp.put("data", getResult(data, "GMV_TOTAL", "2"));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "衰退期");
        tmp.put("data", getResult(data, "GMV_TOTAL", "3"));
        series.add(tmp);
        echart.setSeriesData(series);
        echart.setxAxisData(xdata);
        return echart;
    }

    @Override
    public List<String> getStageNode(String spuId) {
        return spuLifeCycleMapper.getStageNode(spuId);
    }

    /**
     * 阶段随购买次数分布图
     * @param spuId
     * @param type
     * @return
     */
    @Override
    public Echart getStagePeriodData(String spuId, String type) {
        List<Map<String, Object>> dataList = spuLifeCycleMapper.getStagePeriodData(spuId, type);
        Echart echart = new Echart();
        List<String> xdata = dataList.stream().map(x->String.valueOf(x.get("DT_PERIOD"))).collect(Collectors.toList());
        echart.setxAxisData(xdata);
        List<String> seriesData = dataList.stream().map(x->String.valueOf(x.get("PERIOD_NUM"))).collect(Collectors.toList());
        Map<String, Object> series = Maps.newHashMap();
        series.put("data", seriesData);
        echart.setSeriesData(Arrays.asList(series));
        return echart;
    }


//    private List<Double> fixData(Map<String,Double> datas,List<String> periodList)
//    {
//        return periodList.stream().map(s->{
//            s = s.replaceAll("-", "");
//            if(null==datas.get(s)||"".equals(datas.get(s)))
//            {
//                return 0d;
//            }else
//            {
//                return datas.get(s);
//            }
//        }).collect(Collectors.toList());
//    }
}
