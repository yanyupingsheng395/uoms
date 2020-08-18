package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.AddUserMonitorMapper;
import com.linksteady.operate.service.AddUserMonitorService;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @author hxcao
 * @date 2020/8/18
 */
@Service
public class AddUserMonitorServiceImpl implements AddUserMonitorService {

    @Autowired
    private AddUserMonitorMapper addUserMonitorMapper;

    @Override
    public List<Map<String, Object>> getApplySuccessData(String startDt, String endDt, String dateType) {
        List<String> dateList = getDateList(startDt, endDt, dateType);
        String dateFormat = "";
        if(dateType.equalsIgnoreCase("Y")) {
            dateFormat = "yyyy";
        } else if(dateType.equalsIgnoreCase("M")) {
            dateFormat = "yyyy-MM";
        } else if(dateType.equalsIgnoreCase("D")) {
            dateFormat = "yyyy-MM-dd";
        } else {
            throw new IllegalArgumentException("dateType参数值有误！");
        }
        List<Map<String, Object>> newDataList = Lists.newArrayList();
        List<Map<String, Object>> dataList = addUserMonitorMapper.getApplySuccessData(startDt, endDt, dateFormat);
        Map<Object, List<Map<String, Object>>> dataListMap = dataList.stream().collect(Collectors.groupingBy(x -> x.get("add_date")));
        dateList.forEach(x->{
            List<Map<String, Object>> maps = dataListMap.get(x);
            if(maps == null || maps.size() == 0) {
                Map<String, Object> tmp = Maps.newHashMap();
                tmp.put("add_date", x);
                tmp.put("per_cnt", 0);
                tmp.put("sum_cnt", 0);
                newDataList.add(tmp);
            }else {
                newDataList.add(maps.get(0));
            }
        });
        return newDataList;
    }

    @Override
    public Map<String, Object> getConvertCntAndRate(String startDt, String endDt, String dateType) {
        List<String> dateList = getDateList(startDt, endDt, dateType);
        String dateFormat = "";
        if(dateType.equalsIgnoreCase("Y")) {
            dateFormat = "yyyy";
        } else if(dateType.equalsIgnoreCase("M")) {
            dateFormat = "yyyy-MM";
        } else if(dateType.equalsIgnoreCase("D")) {
            dateFormat = "yyyy-MM-dd";
        } else {
            throw new IllegalArgumentException("dateType参数值有误！");
        }
        List<Map<String, Object>> passiveDataList = getPassivePushAndApplyData(startDt, endDt, dateFormat);
        List<Map<String, Object>> triggerDataList = getTriggerPushAndApplyData(startDt, endDt, dateFormat);
        List<Map<String, Object>> newPassiveDataList = Lists.newArrayList();
        List<Map<String, Object>> newTriggerDataList = Lists.newArrayList();
        Map<Object, List<Map<String, Object>>> passiveDataListMap = passiveDataList.stream().collect(Collectors.groupingBy(x -> x.get("add_date")));
        Map<Object, List<Map<String, Object>>> triggerDataListMap = triggerDataList.stream().collect(Collectors.groupingBy(x -> x.get("add_date")));
        dateList.forEach(x->{
            List<Map<String, Object>> maps1 = passiveDataListMap.get(x);
            List<Map<String, Object>> maps2 = triggerDataListMap.get(x);
            if(maps1 == null || maps1.size() == 0) {
                Map<String, Object> tmp = Maps.newHashMap();
                tmp.put("add_date", x);
                tmp.put("apply_pass", 0);
                tmp.put("apply_rate", 0);
                tmp.put("apply_total", 0);
                newPassiveDataList.add(tmp);
            }else {
                newPassiveDataList.add(maps1.get(0));
            }

            if(maps2 == null || maps2.size() == 0) {
                Map<String, Object> tmp = Maps.newHashMap();
                tmp.put("add_date", x);
                tmp.put("apply_pass", 0);
                tmp.put("apply_rate", 0);
                tmp.put("apply_total", 0);
                newTriggerDataList.add(tmp);
            }else {
                newTriggerDataList.add(maps2.get(0));
            }
        });
        List<String> pTotal = newPassiveDataList.stream().map(x -> x.get("apply_total").toString()).collect(Collectors.toList());
        List<String> tTotal = newTriggerDataList.stream().map(x -> x.get("apply_total").toString()).collect(Collectors.toList());
        List<String> pCnt = newPassiveDataList.stream().map(x -> x.get("apply_pass").toString()).collect(Collectors.toList());
        List<String> tCnt = newTriggerDataList.stream().map(x -> x.get("apply_pass").toString()).collect(Collectors.toList());
        List<Double> pRate = newPassiveDataList.stream().map(x -> new BigDecimal(x.get("apply_rate").toString()).doubleValue()).collect(Collectors.toList());
        List<Double> tRate = newTriggerDataList.stream().map(x -> new BigDecimal(x.get("apply_rate").toString()).doubleValue()).collect(Collectors.toList());
        Double pRateAvg = pRate.stream().collect(Collectors.averagingDouble(x -> x));
        Double tRateAvg = tRate.stream().collect(Collectors.averagingDouble(x -> x));
        List<Double> pRateAvgList = DoubleStream.iterate(pRateAvg, n->n).limit(dateList.size()).boxed().collect(Collectors.toList());
        List<Double> tRateAvgList = DoubleStream.iterate(tRateAvg, n->n).limit(dateList.size()).boxed().collect(Collectors.toList());

        Map<String, Object> result = Maps.newHashMap();
        result.put("pTotal", pTotal);
        result.put("tTotal", tTotal);
        result.put("pCnt", pCnt);
        result.put("tCnt", tCnt);
        result.put("pRate", pRate);
        result.put("tRate", tRate);
        result.put("pRateAvgList", pRateAvgList);
        result.put("tRateAvgList", tRateAvgList);
        result.put("dateList", dateList);
        return result;
    }

    /**
     * 获取被动加人转化人数，转化率，总人数
     * @param startDt
     * @param endDt
     * @param dateFormat
     * @return
     */
    private List<Map<String, Object>> getPassivePushAndApplyData(String startDt, String endDt, String dateFormat) {
        return addUserMonitorMapper.getPassivePushAndApplyData(startDt, endDt, dateFormat);
    }

    /**
     * 获取主动加人转化人数，转化率，总人数
     * @param startDt
     * @param endDt
     * @param dateFormat
     * @return
     */
    private List<Map<String, Object>> getTriggerPushAndApplyData(String startDt, String endDt, String dateFormat) {
        return addUserMonitorMapper.getTriggerPushAndApplyData(startDt, endDt, dateFormat);
    }

    /**
     * 获取连续的时间列表
     *
     * @param startDt
     * @param endDt
     * @param dateType
     * @return
     */
    private List<String> getDateList(String startDt, String endDt, String dateType) {
        List<String> dataList = Lists.newArrayList();
        if ("Y".equalsIgnoreCase(dateType)) {
            Year start = Year.parse(startDt, DateTimeFormatter.ofPattern("yyyy"));
            Year end = Year.parse(endDt, DateTimeFormatter.ofPattern("yyyy"));
            while (end.isAfter(start)) {
                dataList.add(start.format(DateTimeFormatter.ofPattern("yyyy")));
                start = start.plusYears(1);
            }
            dataList.add(end.format(DateTimeFormatter.ofPattern("yyyy")));
        }
        if ("M".equalsIgnoreCase(dateType)) {
            YearMonth start = YearMonth.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth end = YearMonth.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM"));
            while (end.isAfter(start)) {
                dataList.add(start.format(DateTimeFormatter.ofPattern("yyyyMM")));
                start = start.plusMonths(1);
            }
            dataList.add(end.format(DateTimeFormatter.ofPattern("yyyyMM")));
        }
        if ("D".equalsIgnoreCase(dateType)) {
            LocalDate start = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            while (end.isAfter(start)) {
                dataList.add(start.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                start = start.plusDays(1);
            }
            dataList.add(end.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        return dataList;
    }
}
