package com.linksteady.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {

    private static transient int gregorianCutoverYear = 1582;

    /** 闰年中每月天数 */
    private static final int[] DAYS_P_MONTH_LY= {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** 非闰年中每月天数 */
    private static final int[] DAYS_P_MONTH_CY= {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** 代表数组里的年、月、日 */
    private static final int Y = 0, M = 1, D = 2;

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
            e.printStackTrace();
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
            e.printStackTrace();
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
}
