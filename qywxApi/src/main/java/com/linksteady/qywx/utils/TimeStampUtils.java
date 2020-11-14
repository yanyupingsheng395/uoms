package com.linksteady.qywx.utils;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 微信中的时间戳转化为java中的日期/日期字符串
 */
public class TimeStampUtils {

    private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间戳转化成日期类型的字符串格式 (针对微信专用)
     * 微信返回的时间戳都是10位精确到秒 而Java中的时间戳都是13位，精确到毫秒，所以毫秒要乘1000
     * 如果要将java中的日期转换成微信的时间戳，则算出毫秒数后除以1000取整 或直接将最后三位截去
     * @param timeStamp
     * @return
     */
    public static String timeStampToDateString(Long timeStamp)
    {
        if(null==timeStamp)
        {
            return "";
        }else
        {
            return sdf.format(new Date(timeStamp*1000));
        }
    }

    /**
     * 时间戳转化成日期类型的字符串格式 (针对微信专用)
     * @param timeStamp
     * @return
     */
    public static String timeStampToDateString(String timeStamp)
    {
        if(StringUtils.isEmpty(timeStamp))
        {
            return "";
        }else
        {
            return sdf.format(new Date(Long.parseLong(timeStamp)*1000));
        }
    }

    /**
     * 时间戳转日期
     * @param timeStamp
     * @return
     */
    public static Date timeStampToDate(String timeStamp)
    {
        if(StringUtils.isEmpty(timeStamp))
        {
            return null;
        }else
        {
            return new Date(Long.parseLong(timeStamp)*1000);
        }
    }

}
