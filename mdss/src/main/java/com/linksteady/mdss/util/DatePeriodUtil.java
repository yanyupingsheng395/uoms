package com.linksteady.mdss.util;

import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Created by hxcao on 2019-06-04
 */
public class DatePeriodUtil {
    private static final String YEAR_FLAG = "Y";
    private static final String MONTH_FLAG = "M";
    private static final String DAY_FLAG = "D";
    /**
     * 获取给定时间的时间区间，时间颗粒度下沉。 年：1-12月，月：1-当月最后一天，天：区间内所有日期
     * @param periodType
     * @param dt 用"～"分割2个日期,日期格式yyyy-MM-dd
     * @param flag 用于判断是获取去年同期，还是上周期
     * @return
     */
    public static List<String> getPeriodDate(String periodType, String dt, Boolean flag) {
        LocalDate now = LocalDate.now();
        List<String> dateList = Lists.newLinkedList();
        if(YEAR_FLAG.equals(periodType)) {
            for(int i=1; i<=12;i++) {
                if(flag) {
                    if(Integer.valueOf(dt) == now.getYear()) {
                        if(i<now.getMonthValue()) {
                            String t = i < 10 ? "0" + i:String.valueOf(i);
                            dateList.add(dt+t);
                        }
                    }
                }
                if(!flag) {
                    String t = i < 10 ? "0" + i:String.valueOf(i);
                    dateList.add(dt+t);
                }
            }
        }
        if(MONTH_FLAG.equals(periodType)) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dt + "-01", df);
            LocalDate firstDay = LocalDate.of(localDate.getYear(),localDate.getMonth(),1);
            LocalDate lastDay =localDate.with(TemporalAdjusters.lastDayOfMonth());

            if(flag) {
                LocalDate lstDay = now.plusDays(-1);
                lastDay = lastDay.isAfter(now) ? lstDay:lastDay;

                if(firstDay.isBefore(now)) {
                    while(firstDay.isBefore(lastDay)) {
                        dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        firstDay = firstDay.plusDays(1);
                    }
                    dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                }
            }else {
                while(firstDay.isBefore(lastDay)) {
                    dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    firstDay = firstDay.plusDays(1);
                }
                dateList.add(firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
        }
        if(DAY_FLAG.equals(periodType)) {
            String[] dateArray = dt.split("~");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDt = LocalDate.parse(dateArray[0], df);
            LocalDate endDt = LocalDate.parse(dateArray[1], df);

            if(flag) {
                LocalDate lstDay = now.plusDays(-1);
                endDt = endDt.isAfter(now) ? lstDay:endDt;
                if(startDt.isBefore(now)) {
                    while(startDt.isBefore(endDt)) {
                        dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        startDt = startDt.plusDays(1);
                    }
                    dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                }
            }else {
                while(startDt.isBefore(endDt)) {
                    dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    startDt = startDt.plusDays(1);
                }
                dateList.add(startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
        }
        return dateList;
    }
}
