package com.linksteady.operate.service.impl;

import afu.org.checkerframework.checker.oigj.qual.O;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.KpiMonitorMapper;
import com.linksteady.operate.dao.OpMapper;
import com.linksteady.operate.domain.DatePeriodKpi;
import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.service.OpService;
import com.linksteady.operate.vo.Echart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class KpiMonitorServiceImpl implements KpiMonitorService {

    @Autowired
    private KpiMonitorMapper kpiMonitorMapper;

    /**
     * 自然月
     */
    public static final String PERIOD_TYPE_MONTH="month";
    /**
     * 间隔月
     */
    public static final String PERIOD_TYPE_INTERVAL_MONTH="dmonth";
    /**
     * 自然周
     */
    public static final String PERIOD_TYPE_WEEK="week";
    /**
     * 间隔周
     */
    public static final String PERIOD_TYPE_INTERVAL_WEEK="dweek";

    public static final String[] D_MONTH_COLS = {"MONTH_ID", "TOTAL_USER", "MONTH1", "MONTH2", "MONTH3", "MONTH4", "MONTH5", "MONTH6", "MONTH7", "MONTH8", "MONTH9", "MONTH10", "MONTH11", "MONTH12"};

    @Override
    public List<WeekInfo> getWeekList(String start, String end) {
        return kpiMonitorMapper.getWeekList(Integer.parseInt(start.replace("-","")),Integer.parseInt(end.replace("-","")));
    }

    /**
     *
     * @param startDt
     * @param endDt
     * @return
     */
    @Override
    public Echart getGMV(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getGMV(startDt, endDt, spuId);

        Map<String, Double> fp = new HashMap<>();
        Map<String, Double> rp = new HashMap<>();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_GMV")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_GMV")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购GMV");
        legendData.add("非首购GMV");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("GMV值（元）");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        tmp.put("name", "首购GMV");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购GMV");
        tmp.put("data", rpList);
        seriesData.add(tmp);

        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getTradeUser(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getTradeUser(startDt, endDt, spuId);

        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_CNT")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_CNT")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("TOTAL_CNT")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购用户数");
        legendData.add("非首购用户数");
        legendData.add("整体用户数");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("交易用户数");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购用户数");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购用户数");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体用户数");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgCsPrice(String startDt, String endDt, String spuId) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgCsPrice(startDt, endDt, spuId);

        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_PRICE")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_PRICE")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("PRICE")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均客单价");
        legendData.add("非首购平均客单价");
        legendData.add("整体平均客单价");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均客单价");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均客单价");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均客单价");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均客单价");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    /**
     * 获取留存率的同期群数据
     * @param period
     * @param begin
     * @return
     */
    @Override
    public Map<String, Object> getRetainData(String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getRetainDMonth(begin, end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getRetainMonth(begin, end);
            return getMonthData(dataList, begin, end, true);
        }
        return null;
    }

    @Override
    public Map<String, Object> getRetentionBySpu(String spuId, String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getRetainBySpuDMonth(spuId, begin, end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getRetainBySpuMonth(spuId, begin, end);
            return getMonthData(dataList, begin, end, true);
        }
        return null;
    }

    /**
     * 获取留存用户数
     * @param period
     * @param begin
     * @return
     */
    @Override
    public Map<String, Object> getRetainUserCount(String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getRetainUserCountDMonth(begin ,end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getRetainMonth(begin, end);
            return getMonthData(dataList, begin, end, false);
        }
        return null;
    }

    @Override
    public Map<String, Object> getRetainUserCountBySpu(String spuId, String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getRetainUserCountDMonthBySpu(spuId, begin ,end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getRetainMonthBySpu(spuId, begin, end);
            return getMonthData(dataList, begin, end, false);
        }
        return null;
    }


    @Override
    public Map<String, Object> getLossUserRate(String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getLossUserRateDMonth(begin, end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getLossUserMonth(begin, end);
            return getMonthData(dataList, begin, end, true);
        }
        return null;
    }

    @Override
    public Map<String, Object> getLossUserBySpu(String spuId, String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getLossUserDMonthBySpu(spuId, begin ,end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getLossUserMonthBySpu(spuId, begin, end);
            return getMonthData(dataList, begin, end, false);
        }
        return null;
    }

    @Override
    public Map<String, Object> getLossUserRateBySpu(String spuId, String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getLossUserRateDMonthBySpu(spuId, begin, end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getLossUserMonthBySpu(spuId, begin, end);
            return getMonthData(dataList, begin, end, true);
        }
        return null;
    }

    @Override
    public Map<String, Object> getLossUser(String period, String begin) {
        String end = null;
        if(StringUtils.isNotBlank(begin)) {
            begin = begin.replaceAll("-", "");
            end = getEndDate(begin);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getLossUserDMonth(begin ,end);
            return getDMonthData(dataList);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getLossUserMonth(begin, end);
            return getMonthData(dataList, begin, end, false);
        }
        return null;
    }

    /**
     * 获取间隔月的数据
     * @return
     */
    public Map<String, Object> getDMonthData(List<Map<String, Object>> dataList) {
        List<String> cols = Arrays.asList(D_MONTH_COLS);
        Map<String, Object> total = Maps.newHashMap();
        cols.stream().forEach(x-> {
            DoubleSummaryStatistics dss = dataList.stream().map(y-> {
                if (!x.equals(D_MONTH_COLS[0])) {
                    return Double.valueOf(y.get(x).toString());
                }
                return 0D;
            }).collect(Collectors.summarizingDouble(v->v));
            if(x.equals(D_MONTH_COLS[1])) {
                total.put(D_MONTH_COLS[1], dss.getSum());
            }else if(!x.equals(D_MONTH_COLS[0])){
                Long count = dataList.stream().filter(z-> z.get(x) != null && Double.valueOf(z.get(x).toString()) > 0D).count();
                total.put(x, count == 0 ? 0:String.format("%.2f", dss.getSum()/count));
            }
        });
        total.put(D_MONTH_COLS[0], "合计:");
        dataList.add(total);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dataList);
        return result;
    }

    /**
     * 获取自然月的数据
     * @param dataList
     * @param begin
     * @param end
     * @param percent 判断是计算率，还是获取数
     * @return
     */
    public Map<String, Object> getMonthData(List<DatePeriodKpi> dataList, String begin, String end, boolean percent) {
        // 将原始数据按各月分组到Map
        Map<String, List<DatePeriodKpi>> datas = getNewData(dataList, begin, end);
        // 分组后的map组装数据到Map
        List<Map<String, Object>> data =  datas.keySet().stream().map(x-> {
            Map<String, Object> tmpMap = Maps.newHashMap();
            List<DatePeriodKpi> list = datas.get(x);
            tmpMap.put("month", x);
            list.stream().forEach(y-> {

                if(percent) {
                    // 月份，新增用户数， 各月的留存率
                    if(y.getBuyPeriod().equals(x)) {
                        tmpMap.put("newUsers", y.getKpiValue());
                    }
                    tmpMap.put(y.getBuyPeriod(), y.getRetention());
                }else {
                    // 月份，新增用户数， 各月的留存率
                    if(y.getBuyPeriod().equals(x)) {
                        tmpMap.put("newUsers", y.getKpiValue());
                    }
                    tmpMap.put(y.getBuyPeriod(), y.getKpiValue());
                }

            });
            return tmpMap;
        }).collect(Collectors.toList());

        // 求合计值
        DoubleSummaryStatistics newUsersTotal = data.stream().map(x->Double.valueOf((String)x.get("newUsers"))).collect(Collectors.summarizingDouble(v->v));
        Map<String, Object> map = Maps.newHashMap();
        map.put("month", "合计：");
        map.put("newUsers", newUsersTotal.getSum());

        List<String> monthPeriod = DateUtil.getMonthBetween(begin, end, "yyyyMM");
        monthPeriod.stream().forEach(t-> {
            DoubleSummaryStatistics tmp = data.stream().map(x->(x.get(t) != null && StringUtils.isNotBlank(x.get(t).toString())) ? Double.valueOf(x.get(t).toString()):0D).collect(Collectors.summarizingDouble(v->v));
            if(percent) {
                Long count = data.stream().filter(x->x.get(t) != null && Double.valueOf(x.get(t).toString()) > 0D).count();
                map.put(t, count == 0 ? 0 : String.format("%.2f", tmp.getSum()/count));
            }else {
                if (tmp.getSum() != 0D) {
                    map.put(t, String.format("%.2f", tmp.getSum()));
                }
            }
        });
        data.add(map);
        List<String> cols = Lists.newArrayList();
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        cols.add("month");
        cols.add("newUsers");
        cols.addAll(monthPeriod);
        result.put("columns", cols);
        return result;
    }



    /**
     * @param data
     * @param beginDt yyyyMM
     * @param endDt yyyyMM
     * @return
     */
    private static Map<String, List<DatePeriodKpi>> getNewData(List<DatePeriodKpi> data, String beginDt, String endDt) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        List<String> monthPeriod = DateUtil.getMonthBetween(beginDt, endDt, "yyyyMM");
        Map<String, List<DatePeriodKpi>> minPeriodKeysMap = data.stream().collect(Collectors.groupingBy(DatePeriodKpi::getMinPeriod, LinkedHashMap::new,Collectors.toList()));
        monthPeriod.stream().map(m-> {
            if(!minPeriodKeysMap.keySet().contains(m)) {
                List<DatePeriodKpi> tmpList = Lists.newArrayList();
                monthPeriod.stream().forEach(m1-> {
                    YearMonth ym1 = YearMonth.parse(m1, df);
                    YearMonth ym2 = YearMonth.parse(m, df);
                    if(m1.equals(m) || ym1.isAfter(ym2)) {
                        tmpList.add(new DatePeriodKpi(m, m1, "0"));
                    }
                });
                minPeriodKeysMap.put(m, tmpList);
            }else {
                List<DatePeriodKpi> tmpList = Lists.newArrayList();
                List<String> ttt = minPeriodKeysMap.get(m).stream().map(o-> o.getBuyPeriod()).collect(Collectors.toList());
                monthPeriod.stream().forEach(t-> {
                    if(!ttt.contains(t) ) {
                        YearMonth ym1 = YearMonth.parse(t, df);
                        YearMonth ym2 = YearMonth.parse(m, df);
                        if(ym1.isAfter(ym2)) {
                            tmpList.add(new DatePeriodKpi(m, t, ""));
                        }
                    }
                });
                tmpList.addAll(minPeriodKeysMap.get(m));
                minPeriodKeysMap.put(m, tmpList);
            }
            return minPeriodKeysMap;
        }).collect(Collectors.toList());

        minPeriodKeysMap.keySet().stream().forEach(c-> {
            List<DatePeriodKpi> list = minPeriodKeysMap.get(c);
            DoubleSummaryStatistics d = list.stream().map(x-> x.getKpiValue().equals("") ? 0D:Double.valueOf(x.getKpiValue())).collect(Collectors.summarizingDouble(v->v));
            list.stream().forEach(x-> {
                double retention = 0D;
                if(d.getSum() != retention) {
                    retention = Double.valueOf(x.getKpiValue().equals("") ? 0D:Double.valueOf(x.getKpiValue())/d.getSum());
                }
                x.setRetention(Double.valueOf(String.format("%.2f", retention)));
            });
        });
        return minPeriodKeysMap;
    }

    private List<Double> fixData(Map<String,Double> datas,List<String> periodList) {
        return periodList.stream().map(s->{
            if(null==datas.get(s)||"".equals(datas.get(s)))
            {
                return 0d;
            }else
            {
                return datas.get(s);
            }
        }).collect(Collectors.toList());
    }

    private String getEndDate(String startDt) {
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
            ex.printStackTrace();
        }
        return null;
    }
}
