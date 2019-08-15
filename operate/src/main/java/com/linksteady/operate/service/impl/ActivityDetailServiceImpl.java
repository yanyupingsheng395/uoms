package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.service.ActivityDetailService;
import com.linksteady.operate.vo.Echart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class ActivityDetailServiceImpl implements ActivityDetailService {

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Override
    public Echart getUserCountData(String startDt, String endDt, String dateRange) {
        Echart echart = new Echart();
        String searchFormat = "yyyy-MM-dd";
        String dateFormat = "yyyyMMdd";

        if(StringUtils.isEmpty(startDt)) {
            startDt = LocalDate.now().format(DateTimeFormatter.ofPattern(searchFormat));
            endDt = LocalDate.now().plusMonths(Long.valueOf(endDt)).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern(searchFormat));
        }

        if(StringUtils.isNotEmpty(dateRange)) {
            startDt = plusDays(startDt, searchFormat, Long.valueOf(dateRange) * -1);
            endDt = plusDays(endDt, searchFormat, Long.valueOf(dateRange));
        }
        List<Map<String, Object>> dataList = activityDetailMapper.getUserCountData(startDt, endDt);

        startDt = DateUtil.convertDateFormat(searchFormat, dateFormat, startDt);
        endDt = DateUtil.convertDateFormat(searchFormat, dateFormat, endDt);
        List<String> datePeriod = DateUtil.getPeriodDate("D", startDt, endDt, dateFormat);
        List<String> yAxisData = getNewData(datePeriod, dataList);

        echart.setxAxisData(datePeriod);
        echart.setyAxisData(yAxisData);
        echart.setxAxisName("日期");
        echart.setyAxisName("用户数");

        return echart;
    }

    /**
     * 补充缺失数据为0
     * @param datePeriod
     * @param dataList
     * @return
     */
    private List<String> getNewData(List<String> datePeriod, List<Map<String, Object>> dataList) {
        // 日期格式转换为统一[yyyyMMdd]
        Map<String, Object> dateAndCountMap = Maps.newHashMap();
        dataList.forEach(x->dateAndCountMap.put(x.get("DT").toString(), x.get("USER_COUNT")));
        List<String> data = Lists.newArrayList();
        datePeriod.forEach(x->{
            if(dateAndCountMap.get(x) == null) {
                data.add("0");
            }else {
                data.add(dateAndCountMap.get(x).toString());
            }
        });
        return data;
    }

    /**
     * 日期加N天
     * @param date
     * @param format
     * @param plusDay
     * @return
     */
    private String plusDays(String date, String format, Long plusDay) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
        return localDate.plusDays(plusDay).format(DateTimeFormatter.ofPattern(format));
    }
}
