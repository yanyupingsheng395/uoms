package com.linksteady.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class DateUtil {

    private static transient int gregorianCutoverYear = 1582;

    /** 闰年中每月天数 */
    private static final int[] DAYS_P_MONTH_LY= {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** 非闰年中每月天数 */
    private static final int[] DAYS_P_MONTH_CY= {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** 代表数组里的年、月、日 */
    private static final int Y = 0, M = 1, D = 2;

    private static final String YEAR = "Y", MONTH = "M", DAY = "D";

    private DateUtil(){

    }

    /**
     * 获取两个月份之间所有的月列表 minDate,maxDate均为YYYY-MM格式
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<String> getMonthBetween(String minDate, String maxDate){
        ArrayList<String> result = new ArrayList<String>();
        //格式化为年月
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        Date minDt= null;
        Date maxDt= null;
        try {
            minDt = sdf.parse(minDate);
            maxDt = sdf.parse(maxDate);
        } catch (ParseException e) {
            minDt=new Date();
            maxDt=new Date();
        }

        min.setTime(minDt);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(maxDt);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }


    public static List<String> getMonthBetween(String minDate, String maxDate, String format){
        ArrayList<String> result = new ArrayList<>();
        //格式化为年月
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        Date minDt= null;
        Date maxDt= null;
        try {
            minDt = sdf.parse(minDate);
            maxDt = sdf.parse(maxDate);
        } catch (ParseException e) {
            minDt=new Date();
            maxDt=new Date();
        }

        min.setTime(minDt);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(maxDt);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    /**
     * 以循环的方式计算日期
     * @param beginDate endDate
     * @param endDate
     * @return
     */
    public static List<String> getEveryday(String beginDate , String endDate){
        long days = countDay(beginDate, endDate);
        int[] ymd = splitYMD( beginDate );
        List<String> everyDays = new ArrayList<>();
        everyDays.add(beginDate);
        for(int i = 0; i < days; i++){
            ymd = addOneDay(ymd[Y], ymd[M], ymd[D]);
            everyDays.add(ymd[Y]+"-"+String.format("%02d",ymd[M])+"-"+String.format("%02d",ymd[D]));
        }
        return everyDays;
    }

    /**
     * 计算两个日期之间相隔的天数
     * @param begin
     * @param end
     * @return
     * @throws ParseException
     */
    public static long countDay(String begin,String end){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate , endDate;
        long day = 0;
        try {
            beginDate= format.parse(begin);
            endDate= format.parse(end);
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
        } catch (ParseException e) {
            log.error("Exception:", e);
        }
        return day;
    }

    /**
     * 将代表日期的字符串分割为代表年月日的整形数组
     * @param date
     * @return
     */
    public static int[] splitYMD(String date){
        date = date.replace("-", "");
        int[] ymd = {0, 0, 0};
        ymd[Y] = Integer.parseInt(date.substring(0, 4));
        ymd[M] = Integer.parseInt(date.substring(4, 6));
        ymd[D] = Integer.parseInt(date.substring(6, 8));
        return ymd;
    }

    /**
     * 检查传入的参数代表的年份是否为闰年
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        return year >= gregorianCutoverYear ?
                ((year%4 == 0) && ((year%100 != 0) || (year%400 == 0))) : (year%4 == 0);
    }

    /**
     * 日期加1天
     * @param year
     * @param month
     * @param day
     * @return
     */
    private static int[] addOneDay(int year, int month, int day){
        if(isLeapYear( year )){
            day++;
            if( day > DAYS_P_MONTH_LY[month -1 ] ){
                month++;
                if(month > 12){
                    year++;
                    month = 1;
                }
                day = 1;
            }
        }else{
            day++;
            if( day > DAYS_P_MONTH_CY[month -1 ] ){
                month++;
                if(month > 12){
                    year++;
                    month = 1;
                }
                day = 1;
            }
        }
        int[] ymd = {year, month, day};
        return ymd;
    }

    /** 
        * 根据原来的month 获得相对偏移 N 月的月份（month） 
        * @param protoDate 原来的月份
        * @param dateOffset（向前移正数，向后移负数） 
        * @return 月份（如201901） 
     */
    public static String getOffsetMonthDate(String month ,int monthOffset){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(month));
        } catch (ParseException e) {
            log.error("Exception:", e);
        }
        cal.add(Calendar.MONTH, -monthOffset);
        return  sdf.format(cal.getTime());
    }

    /**
     * 根据输入的YYYY-MM格式的月份获取当月的最后一天 返回YYYY-MM-DD格式
     * @param yearMonth
     * @return
     */
    public static String getLastDayOfMonth(String yearMonth) {
        //年
        int year = Integer.parseInt(yearMonth.split("-")[0]);
        //月
        int month = Integer.parseInt(yearMonth.split("-")[1]);
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    /**
     * 计算起始日期间隔天数
     * @return
     */
    public static int getDaysDuringDates(Date startDt, Date endDt) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startDt);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endDt);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++) {
                if(i % 4 == 0 && i % 100 != 0 || i % 400==0) {
                    timeDistance += 366;
                } else {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2-day1) ;
        } else {
            return day2-day1;
        }
    }

    /**
     * 获取去年同期
     * @param dateStr yyyy-MM
     * @return
     */
    public static String getLastYear(String dateStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth ym = YearMonth.parse(dateStr, df);
        ym = ym.plusYears(Period.ofYears(-1).getYears());
        String result = ym.format(df);
        return result;
    }


    /**
     * 获取上周期
     * @param dateStr yyyy-MM
     * @return
     */
    public static String getLast(String dateStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth ym = YearMonth.parse(dateStr, df);
        ym = ym.plusMonths(Period.ofMonths(-1).getMonths());
        String result = ym.format(df);
        return result;
    }

    /**
     * 获取天到天的上周期
     * @return
     */
    public static Map<String, Object> getLastOfDay(String startDt, String endDt) {
        Map<String, Object> result = Maps.newHashMap();
        LocalDate startDt0 = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDt0 = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        LocalDate lastEndDt = startDt0;
        int span = startDt0.getDayOfYear()-endDt0.getDayOfYear();
        LocalDate lastStartDt = lastEndDt.plusDays(Period.ofDays(span).getDays());
        result.put("start", lastStartDt);
        result.put("end", lastEndDt);
        return result;
    }

    public static Map<String, Object> getLastYearOfDay(String startDt, String endDt) {
        Map<String, Object> result = Maps.newHashMap();
        LocalDate startDt0 = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDt0 = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        LocalDate lastYearStartDt = startDt0.plusYears(Period.ofYears(-1).getYears());
        LocalDate lastYearEndDt = endDt0.plusYears(Period.ofYears(-1).getYears());
        result.put("start", lastYearStartDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.put("end", lastYearEndDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return result;
    }

    /**
     * 同期群：根据开时间计算结束日期，如果开始时间到当前时间小于12个月，结束时间为当前日期，否则开始时间往后推12个月
     * @param startDt 开始时间，格式：yyyyMM
     * @return
     */
    public static String getEndDateOfMonth(String startDt) {
        try {
            DateFormat df = new SimpleDateFormat("yyyyMM");
            Date startDate = df.parse(startDt);

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);

            Calendar endCal = Calendar.getInstance();
            endCal.add(Calendar.MONTH, 12);

            Calendar current = Calendar.getInstance();

            if(endCal.after(current)) {
                return df.format(current.getTime());
            }else {
                return df.format(endCal.getTime());
            }
        }catch (Exception ex) {
            log.error("获取结束日期异常：", ex);
        }
        return null;
    }

    /**
     * 获取两个日期间隔的月数
     * @param startDt
     * @param endDt
     * @return
     */
    public static Integer getPeriod(String startDt, String endDt) {
        YearMonth start = YearMonth.parse(startDt, DateTimeFormatter.ofPattern("yyyyMM"));
        YearMonth end = YearMonth.parse(endDt, DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth(), 1);
        LocalDate endDate = LocalDate.of(end.getYear(), end.getMonth(), 1);
        return Period.between(startDate, endDate).getYears() * 12 + Period.between(startDate, endDate).getMonths();
    }

    /**
     * 获取某个间隔后的日期
     * @param startDt
     * @param interval
     * @return
     */
    public static String getDateByPlusMonth(String startDt, int interval) {
        YearMonth start = YearMonth.parse(startDt, DateTimeFormatter.ofPattern("yyyyMM"));
        YearMonth end = start.plusMonths(interval);
        return end.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    /**
     * 获取年月日起止日期的时间段
     * @param type
     * @param start
     * @param end
     * @return
     */
    public static List<String> getPeriodDate(String type, String start, String end) {
        List<String> dateList = Lists.newLinkedList();
        if(YEAR.equals(type)) {
            LocalDate startDt = LocalDate.of(Integer.valueOf(start),1,1);
            LocalDate endDt = LocalDate.of(Integer.valueOf(end),1,1);
            do {
                dateList.add(String.valueOf(startDt.getYear()));
                startDt = startDt.plusYears(1);
            }while (startDt.isBefore(endDt) || startDt.equals(endDt));
        }
        if(MONTH.equals(type)) {
            String format = "yyyy-MM";
            YearMonth startDt = YearMonth.parse(start, DateTimeFormatter.ofPattern(format));
            YearMonth endDt = YearMonth.parse(end, DateTimeFormatter.ofPattern(format));
            do {
                dateList.add(startDt.format(DateTimeFormatter.ofPattern(format)));
                startDt = startDt.plusMonths(1);
            }while (startDt.isBefore(endDt) || startDt.equals(endDt));
        }
        if(DAY.equals(type)) {
            String format = "yyyy-MM-dd";
            LocalDate startDt = LocalDate.parse(start, DateTimeFormatter.ofPattern(format));
            LocalDate endDt = LocalDate.parse(end, DateTimeFormatter.ofPattern(format));
            do {
                dateList.add(startDt.format(DateTimeFormatter.ofPattern(format)));
                startDt = startDt.plusDays(1);
            }while (startDt.isBefore(endDt) || startDt.equals(endDt));
        }
        return dateList;
    }

    public static List<String> getPeriodDate(String type, String start, String end, String format) {
        List<String> dateList = Lists.newLinkedList();
        if(YEAR.equals(type)) {
            LocalDate startDt = LocalDate.of(Integer.valueOf(start),1,1);
            LocalDate endDt = LocalDate.of(Integer.valueOf(end),1,1);
            do {
                dateList.add(String.valueOf(startDt.getYear()));
                startDt = startDt.plusYears(1);
            }while (startDt.isBefore(endDt) || startDt.equals(endDt));
        }
        if(MONTH.equals(type)) {
            YearMonth startDt = YearMonth.parse(start, DateTimeFormatter.ofPattern(format));
            YearMonth endDt = YearMonth.parse(end, DateTimeFormatter.ofPattern(format));
            do {
                dateList.add(startDt.format(DateTimeFormatter.ofPattern(format)));
                startDt = startDt.plusMonths(1);
            }while (startDt.isBefore(endDt) || startDt.equals(endDt));
        }
        if(DAY.equals(type)) {
            LocalDate startDt = LocalDate.parse(start, DateTimeFormatter.ofPattern(format));
            LocalDate endDt = LocalDate.parse(end, DateTimeFormatter.ofPattern(format));
            do {
                dateList.add(startDt.format(DateTimeFormatter.ofPattern(format)));
                startDt = startDt.plusDays(1);
            }while (startDt.isBefore(endDt) || startDt.equals(endDt));
        }
        return dateList;
    }

    /**
     * 2个日期格式的转化
     * @param sourceFormat
     * @param targetFormat
     * @param date
     * @return
     */
    public static String convertDateFormat(String sourceFormat, String targetFormat, String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(sourceFormat));
        return localDate.format(DateTimeFormatter.ofPattern(targetFormat));
    }

    /**
     * 日期加N天
     * @param date
     * @param format
     * @param plusDay
     * @return
     */
    public static String plusDays(String date, String format, Long plusDay) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
        return localDate.plusDays(plusDay).format(DateTimeFormatter.ofPattern(format));
    }
}