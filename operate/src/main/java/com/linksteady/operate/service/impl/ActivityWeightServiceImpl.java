package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityWeightMapper;
import com.linksteady.operate.service.ActivityWeightService;
import com.linksteady.operate.vo.Echart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-14
 */
@Service
public class ActivityWeightServiceImpl implements ActivityWeightService {

    @Autowired
    private ActivityWeightMapper activityWeightMapper;

    @Override
    public Echart getWeightIdx(String startDt, String endDt, String dateRange) {

        Echart echart = new Echart();
        String dateFormat = "yyyyMMdd";
        String searchFormat = "yyyy-MM-dd";

        if(StringUtils.isNotEmpty(dateRange)) {
            startDt = DateUtil.plusDays(startDt, searchFormat, Long.valueOf(dateRange) * -1);
            endDt = DateUtil.plusDays(endDt, searchFormat, Long.valueOf(dateRange));
        }

        List<Map<String, Object>> dataList = activityWeightMapper.getWeightIdx(startDt, endDt);

        startDt = DateUtil.convertDateFormat(searchFormat, dateFormat, startDt);
        endDt = DateUtil.convertDateFormat(searchFormat, dateFormat, endDt);
        List<String> datePeriod = DateUtil.getPeriodDate("D", startDt, endDt, dateFormat);
        List<String> yAxisData = getNewData(datePeriod, dataList);

        echart.setxAxisData(datePeriod);
        echart.setyAxisData(yAxisData);
        echart.setxAxisName("日期");
        echart.setyAxisName("权重指数");

        return echart;

    }

    /**
     * 补充缺失数据为0
     * @param datePeriod
     * @param dataList
     * @return
     */
    private List<String> getNewData(List<String> datePeriod, List<Map<String, Object>> dataList) {
        DecimalFormat df = new DecimalFormat("#.00");
        Map<String, Object> dateAndCountMap = Maps.newHashMap();
        dataList.forEach(x->dateAndCountMap.put(x.get("DT").toString(), x.get("WEIGHT_IDX")));
        List<String> data = Lists.newArrayList();
        datePeriod.forEach(x->{
            if(dateAndCountMap.get(x) == null) {
                data.add("0");
            }else {
                data.add(df.format(dateAndCountMap.get(x)));
            }
        });
        return data;
    }

}
