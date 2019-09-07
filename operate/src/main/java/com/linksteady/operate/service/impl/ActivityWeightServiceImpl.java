package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityWeightMapper;
import com.linksteady.operate.domain.ActivityWeight;
import com.linksteady.operate.service.ActivityWeightService;
import com.linksteady.operate.vo.Echart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 对于大促活动，获取其影响日期
     * @param beginDate 开始日期 YYYY-MM-DD格式
     * @param endDate 结束日期 YYYY-MM-DD格式
     * @return map结构，key为begin 表示对应的value为影响开始日期 key为end 表示对应的value为影响结束日期 value为YYYY-MM-DD格式
     */
    @Override
    public Map<String,String> getEffectDate(String beginDate,String endDate){
        Map<String,String> result=Maps.newHashMap();

       String effectBeginDate="";
       String effectEndDate="";

        //判断活动开始前5天的权重指数  日期越大，索引值越小
        List<ActivityWeight> list=activityWeightMapper.getTop5weightForBegin(beginDate);

        if(null==list&&list.size()<5)
        {
            effectBeginDate=beginDate;
        }else
        {
            //判断大于1.4的有几天
            List<ActivityWeight> gtlist=list.stream().filter(x->x.getWeightIdx()>=1.4).collect(Collectors.toList());
            int count=gtlist.size();

            if(count==5)
            {
                 //继续向前取3天的数据
                List<ActivityWeight> top8List=activityWeightMapper.getTop8weightForBegin(beginDate);
                List<ActivityWeight> top8Gtlist=top8List.stream().filter(x->x.getWeightIdx()>=1.4).collect(Collectors.toList());

                if(top8List.size()<3)
                {
                    //如果再向前取不到3个 则为之前(五天只中)第一个>=1.4的的节点为建议开始日期
                    LocalDate date1=dateToLocalDate(gtlist.get(0).getActivDt());
                    effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }else
                {
                    if(top8Gtlist.size()==0)
                    {
                        //如果不存在>=1.4的情况 则为之前(五天只中)第一个>=1.4的的节点为建议开始日期
                        LocalDate date1=dateToLocalDate(gtlist.get(0).getActivDt());
                        effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }else if(top8Gtlist.size()==1)
                    {
                        //如果只有1天>=1.4，且这1天与后面5天的差小于等于1天，则取这天的时间作为活动影响的开始时间，如果这1天与后面5天的差=2天，则忽略掉这一天，直接取后面连续5天的第1天作为活动影响的开始时间。
                        LocalDate date1=dateToLocalDate(top8Gtlist.get(0).getActivDt());
                        LocalDate date2=dateToLocalDate(gtlist.get(0).getActivDt());

                        if(date1.until(date2,ChronoUnit.DAYS)-1<=1)
                        {
                            effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }else
                        {
                            effectBeginDate=date2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }

                    }else
                    {
                        //取第一个>=1.4的日期作为活动建议日期
                        LocalDate date1=dateToLocalDate(top8Gtlist.get(0).getActivDt());
                        effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                }


            }else if(count==4 || count==3)
            {
                 //取第一个大于1.4的日期作为活动建议的第一天
                LocalDate date1=dateToLocalDate(gtlist.get(0).getActivDt());
                effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            }else if(count==2)
            {
                 //如果有2天>=1.4，2天日期相差小于等于2，则取第一天>=1.4的日期作为活动影响的开始时间，如果2天相差3天，则取第2个>=1.4的日期作为活动影响的开始时间；
                LocalDate date1=dateToLocalDate(gtlist.get(0).getActivDt());
                LocalDate date2=dateToLocalDate(gtlist.get(1).getActivDt());

                if(date1.until(date2, ChronoUnit.DAYS)-1<=2)
                {
                    effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }else
                {
                    effectBeginDate=date2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            }else if(count==1)
            {
                //只有一个日期权重指数>=1.4  且与活动开始日期相差<=2天，取该天的日期为活动建议的第一天 否则取 活动开始日期作为建议的一天
                LocalDate date1=dateToLocalDate(gtlist.get(0).getActivDt());
                //活动开始日期
                LocalDate date2= LocalDate.parse(beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                if(date1.until(date2, ChronoUnit.DAYS)-1<=2)
                {
                    effectBeginDate=date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }else
                {
                    effectBeginDate=beginDate;
                }
            }else
            {
                effectBeginDate=beginDate;
            }

        }

        //获取结束日期的影响天数
        LocalDate date3=LocalDate.parse(beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate date4=LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //获取开始日期所在的年
        int beanYear=date3.getYear();
        LocalDate d11=LocalDate.parse(beanYear+"-11-11", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate d618=LocalDate.parse(beanYear+"-06-18", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if(date3.isBefore(d11)&&date4.isAfter(d11))
        {
            //获取结束日期所在的最后一天
            LocalDate lastDay =date4.with(TemporalAdjusters.lastDayOfMonth());
            effectEndDate=lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else if(date3.isBefore(d618)&&date4.isAfter(d618))
        {
            //获取结束日期所在的最后一天
            LocalDate lastDay =date4.with(TemporalAdjusters.lastDayOfMonth());
            effectEndDate=lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else {
             //活动结束时间之后权重指数从小于1变为大于1的那个临界值作为受影响的结束日期
            ActivityWeight activityWeight= activityWeightMapper.getEffectDateForEnd(endDate);

            if(null==activityWeight)
            {
                effectEndDate=endDate;
            }else
            {
                effectEndDate=dateToLocalDate(activityWeight.getActivDt()).minusDays(1).format( DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }

        result.put("begin",effectBeginDate);
        result.put("end",effectEndDate);
        return result;
    }

    private LocalDate dateToLocalDate(Date date)
    {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }


}
