package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.SpuLifeCycleMapper;
import com.linksteady.operate.service.SpuLifeCycleService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    @Override
    public Echart retentionPurchaseTimes(String spuId) {
        Echart echart = new Echart();
        List<Map<String, Object>> data = spuLifeCycleMapper.retentionPurchaseTimes(spuId);
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
        List list = Lists.newArrayList();
        list.add(tmp);
        echart.setSeriesData(list);
        return echart;
    }


    @Override
    public Echart getPurchDateChart(String spuId, Integer gt, Integer lt) throws Exception {
        List<Map<String, Object>> data = spuLifeCycleMapper.getPurchDate(spuId, gt, lt);
        // todo startDt取样时间，后续传值
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String startDt = "20190401";
        Calendar endDt = Calendar.getInstance();
        endDt.add(Calendar.DATE, -1);
        int days = DateUtil.getDaysDuringDates(df.parse(startDt), endDt.getTime());

        List<String> daysList = Lists.newArrayList();
        List<String> dataList = Lists.newArrayList();
        for(int i=1; i<=days; i++) {
            boolean flag = false;
            daysList.add(String.valueOf(i));
            for(int j=0; j<data.size(); j++) {
                Map<String, Object> t = data.get(j);
                try {
                    int daysTmp = DateUtil.getDaysDuringDates(df.parse(String.valueOf(t.get("SPU_DT"))), endDt.getTime());
                    if(daysTmp == i) {
                        flag = true;
                        dataList.add(String.valueOf(t.get("PURCH_TIMES")));
                        break;
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if(!flag) {
                dataList.add("0");
            }
        }

        Echart echart = new Echart();
        echart.setxAxisData(daysList);
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
        echart.setyAxisName("连带率（%）");

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

        Map<String,Double> datas1 = Maps.newHashMap();
        Map<String,Double> datas2 = Maps.newHashMap();
        Map<String,Double> datas3 = Maps.newHashMap();
        Map<String,Double> datas4 = Maps.newHashMap();
        data.stream().forEach(t-> {
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("0")) {
                datas1.put(String.valueOf(t.get("SPU_DT")), ((BigDecimal)(t.get("USER_COUNT"))).doubleValue());
            }
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("1")) {
                datas2.put(String.valueOf(t.get("SPU_DT")), ((BigDecimal)(t.get("USER_COUNT"))).doubleValue());
            }
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("2")) {
                datas3.put(String.valueOf(t.get("SPU_DT")), ((BigDecimal)(t.get("USER_COUNT"))).doubleValue());
            }
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("3")) {
                datas4.put(String.valueOf(t.get("SPU_DT")), ((BigDecimal)(t.get("USER_COUNT"))).doubleValue());
            }
        });

        List<String> dayPeriodList=DateUtil.getEveryday("2019-05-01","2019-05-31");
        List<Map<String, Object>> series = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("name", "新客期");
        tmp.put("data", fixData(datas1, dayPeriodList));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成长期");
        tmp.put("data", fixData(datas2, dayPeriodList));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成熟期");
        tmp.put("data", fixData(datas3, dayPeriodList));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "衰退期");
        tmp.put("data", fixData(datas4, dayPeriodList));
        series.add(tmp);
        echart.setSeriesData(series);
        echart.setxAxisData(dayPeriodList);
        return echart;
    }

    @Override
    public Echart getSaleVolumeChart(String spuId) {
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

        Map<String,Double> datas1 = Maps.newHashMap();
        Map<String,Double> datas2 = Maps.newHashMap();
        Map<String,Double> datas3 = Maps.newHashMap();
        Map<String,Double> datas4 = Maps.newHashMap();
        data.stream().forEach(t-> {
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("0")) {
                datas1.put(String.valueOf(t.get("SPU_DT")), Double.valueOf((String)(t.get("SALE_VOLUME"))));
            }
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("1")) {
                datas2.put(String.valueOf(t.get("SPU_DT")), Double.valueOf((String)(t.get("SALE_VOLUME"))));
            }
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("2")) {
                datas3.put(String.valueOf(t.get("SPU_DT")), Double.valueOf((String)(t.get("SALE_VOLUME"))));
            }
            if(t.get("LIFECYCLE_TYPE") != null && t.get("LIFECYCLE_TYPE").equals("3")) {
                datas4.put(String.valueOf(t.get("SPU_DT")), Double.valueOf((String)(t.get("SALE_VOLUME"))));
            }
        });

        List<String> dayPeriodList=DateUtil.getEveryday("2019-05-01","2019-05-31");
        List<Map<String, Object>> series = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        tmp.put("name", "新客期");
        tmp.put("data", fixData(datas1, dayPeriodList));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成长期");
        tmp.put("data", fixData(datas2, dayPeriodList));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "成熟期");
        tmp.put("data", fixData(datas3, dayPeriodList));
        series.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "衰退期");
        tmp.put("data", fixData(datas4, dayPeriodList));
        series.add(tmp);
        echart.setSeriesData(series);
        echart.setxAxisData(dayPeriodList);
        return echart;
    }

    @Override
    public List<String> getStageNode(String spuId) {
        return spuLifeCycleMapper.getStageNode(spuId);
    }


    private List<Double> fixData(Map<String,Double> datas,List<String> periodList)
    {
        return periodList.stream().map(s->{
            s = s.replaceAll("-", "");
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
