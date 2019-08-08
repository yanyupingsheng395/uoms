package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyExecuteMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.DailyExecute;
import com.linksteady.operate.service.DailyExecuteService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-08-06
 */
@Service
public class DailyExecuteServiceImpl implements DailyExecuteService {

    @Autowired
    private DailyExecuteMapper dailyExecuteMapper;

    @Autowired
    private DailyMapper dailyMapper;

    private static final Long PERIOD = 15L;

    @Override
    public Map<String, Echart> getKpiTrend(String headId) {
        Map<String, Echart> result = Maps.newHashMap();
        String touchDt = dailyMapper.getTouchDt(headId);
        List<String> dateList = Lists.newLinkedList();
        Long period = 0L;
        while(period < PERIOD) {
            LocalDate localDate = LocalDate.parse(touchDt, DateTimeFormatter.ofPattern("yyyyMMdd"));
            localDate = localDate.plusDays(1);
            touchDt = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            dateList.add(touchDt);
            period++;
        }
        List<DailyExecute> executeList = dailyExecuteMapper.getKpiTrend(headId);
        Map<String, DailyExecute> dataMap = executeList.stream().collect(Collectors.toMap(DailyExecute::getExecuteDt, Function.identity()));

        List<String> convertAmountList = dateList.stream().map(x->{
            if(dataMap.get(x) != null) {
                return dataMap.get(x).getConvertAmount().toString();
            }else {
                return null;
            }
        }).collect(Collectors.toList());

        List<String> convertCntList = dateList.stream().map(x->{
            if(dataMap.get(x) != null) {
                return dataMap.get(x).getConvertCnt().toString();
            }else {
                return null;
            }
        }).collect(Collectors.toList());

        List<String> sellCntList = dateList.stream().map(x->{
            if(dataMap.get(x) != null) {
                return dataMap.get(x).getSellCnt().toString();
            }else {
                return null;
            }
        }).collect(Collectors.toList());


        Echart echart1 = new Echart();
        echart1.setxAxisName("日期");
        echart1.setxAxisData(dateList);
        echart1.setyAxisData(convertCntList);
        echart1.setyAxisName("任务转化人数（人）");

        Echart echart2 = new Echart();
        echart2.setxAxisName("日期");
        echart2.setxAxisData(dateList);
        echart2.setyAxisData(sellCntList);
        echart2.setyAxisName("补贴核销数（人）");

        Echart echart3 = new Echart();
        echart3.setxAxisName("日期");
        echart3.setxAxisData(dateList);
        echart3.setyAxisData(convertAmountList);
        echart3.setyAxisName("任务转化金额（元）");

        result.put("convertCnt", echart1);
        result.put("sellCnt", echart2);
        result.put("convertAmount", echart3);

        return result;
    }
}
