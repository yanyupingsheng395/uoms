package com.linksteady.operate.service.impl;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.KpiMonitorMapper;
import com.linksteady.operate.domain.DatePeriodKpi;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.vo.Echart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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

    // 客单价
    public static final String[] D_MONTH_UPRICE = {"MONTH_ID", "TOTAL_USER", "UPRICE", "UPRICE1", "UPRICE2", "UPRICE3", "UPRICE4", "UPRICE5", "UPRICE6", "UPRICE7", "UPRICE8", "UPRICE9", "UPRICE10", "UPRICE11", "UPRICE12"};

    // 订单价
    public static final String[] D_MONTH_PRICE = {"MONTH_ID", "TOTAL_USER", "PRICE", "PRICE1", "PRICE2", "PRICE3", "PRICE4", "PRICE5", "PRICE6", "PRICE7", "PRICE8", "PRICE9", "PRICE10", "PRICE11", "PRICE12"};

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
        List<Map<String, Object>> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(spuId)) {
            list = kpiMonitorMapper.getGMVBySpu(startDt, endDt, spuId);
        }else {
            list = kpiMonitorMapper.getGMV(startDt, endDt);
        }

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
        List<Map<String, Object>> list  = Lists.newArrayList();
        if(StringUtils.isNotBlank(spuId)) {
            list = kpiMonitorMapper.getTradeUserBySpu(startDt, endDt, spuId);
        }else {
            list = kpiMonitorMapper.getTradeUser(startDt, endDt);
        }
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
        List<Map<String, Object>> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(spuId)) {
            list = kpiMonitorMapper.getAvgCsPriceBySpu(startDt, endDt, spuId);
        }else {
            list = kpiMonitorMapper.getAvgCsPrice(startDt, endDt);
        }
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
            return getDMonthData(dataList, true);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            //自然月数据
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getRetainMonth(begin, end);
            return getMonthPercentData(dataList, begin, end);
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
            return getDMonthData(dataList, true);
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
            return getDMonthData(dataList,false);
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
            return getDMonthData(dataList,false);
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
            return getDMonthData(dataList, true);
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
            return getDMonthData(dataList, false);
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
            return getDMonthData(dataList,true);
        }
        if(PERIOD_TYPE_MONTH.equals(period)) {
            List<DatePeriodKpi> dataList =  kpiMonitorMapper.getLossUserMonthBySpu(spuId, begin, end);
            return getMonthData(dataList, begin, end, true);
        }
        return null;
    }

    /**
     * 同期群客单价分析
     * @param periodType
     * @param start
     * @return
     */
    @Override
    public Map<String, Object> getUpriceData(String periodType, String start) {
        String end = null;
        if(StringUtils.isNotBlank(start)) {
            start = start.replaceAll("-", "");
            end = getEndDate(start);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getUpriceData(start, end);
            return getUPriceDMonthData(dataList, true);
        }
        if(PERIOD_TYPE_MONTH.equals(periodType)) {
            List<DatePeriodKpi> dataList = kpiMonitorMapper.getUpriceDataMonth(start, end);
            return getUPriceMonthData(dataList, start, end, true);
        }
        return null;
    }

    @Override
    public Map<String, Object> getUpriceDataBySpu(String spuId, String periodType, String start) {
        String end = null;
        if(StringUtils.isNotBlank(start)) {
            start = start.replaceAll("-", "");
            end = getEndDate(start);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getUpriceDataBySpu(spuId, start, end);
            return getUPriceDMonthData(dataList, true);
        }
        if(PERIOD_TYPE_MONTH.equals(periodType)) {
            List<DatePeriodKpi> dataList = kpiMonitorMapper.getUpriceDataMonthBySpu(spuId, start, end);
            return getUPriceMonthData(dataList, start, end, true);
        }
        return null;
    }


    @Override
    public Map<String, Object> getPriceData(String periodType, String start) {
        String end = null;
        if(StringUtils.isNotBlank(start)) {
            start = start.replaceAll("-", "");
            end = getEndDate(start);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getPriceData(start, end);
            return getPriceDMonthData(dataList, true);
        }
        if(PERIOD_TYPE_MONTH.equals(periodType)) {
            List<DatePeriodKpi> dataList = kpiMonitorMapper.getPriceDataMonth(start, end);
            return getUPriceMonthData(dataList, start, end, true);
        }
        return null;
    }

    @Override
    public Map<String, Object> getPriceDataBySpu(String spuId, String periodType, String start) {
        String end = null;
        if(StringUtils.isNotBlank(start)) {
            start = start.replaceAll("-", "");
            end = getEndDate(start);
        }
        if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType)) {
            List<Map<String, Object>> dataList = kpiMonitorMapper.getPriceDataBySpu(spuId, start, end);
            return getPriceDMonthData(dataList, true);
        }
        if(PERIOD_TYPE_MONTH.equals(periodType)) {
            List<DatePeriodKpi> dataList = kpiMonitorMapper.getPriceDataMonthBySpu(spuId, start, end);
            return getUPriceMonthData(dataList, start, end, true);
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
            return getDMonthData(dataList, false);
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
    public Map<String, Object> getDMonthData(List<Map<String, Object>> dataList, boolean percent) {
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
                if(percent) {
                    Long count = dataList.stream().filter(z-> z.get(x) != null && Double.valueOf(z.get(x).toString()) > 0D).count();
                    total.put(x, count == 0 ? 0:String.format("%.2f", dss.getSum()/count));
                }else {
                    total.put(x, dss.getSum());
                }
            }
        });
        total.put(D_MONTH_COLS[0], "合计:");
        dataList.add(total);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dataList);
        return result;
    }

    /**
     * 客单价间隔月
     * @param dataList
     * @return
     */
    public Map<String, Object> getUPriceDMonthData(List<Map<String, Object>> dataList, boolean percent) {
        DecimalFormat df = new DecimalFormat("#");
        List<String> cols = Arrays.asList(D_MONTH_UPRICE);
        Map<String, Object> total = Maps.newLinkedHashMap();
        cols.stream().forEach(x-> {
            DoubleSummaryStatistics dss = dataList.stream().map(y-> {
                if (!x.equals(D_MONTH_UPRICE[0])) {
                    return y.get(x) == null ? 0:Double.valueOf(y.get(x).toString());
                }
                return 0D;
            }).collect(Collectors.summarizingDouble(v->v));

            // 计算本月新增用户数的和
            if(x.equals(D_MONTH_UPRICE[1])) {
                total.put(D_MONTH_UPRICE[1], dss.getSum());
            }else if(!x.equals(D_MONTH_UPRICE[0])){
                if(percent) {
                    Long count = dataList.stream().filter(z-> z.get(x) != null && Double.valueOf(z.get(x).toString()) > 0D).count();
                    total.put(x, count == 0 ? 0:df.format(dss.getSum()/count));
                }else {
                    total.put(x, dss.getSum());
                }
            }
        });
        total.put(D_MONTH_UPRICE[0], "合计:");
        dataList.add(total);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dataList);
        return result;
    }

    public Map<String, Object> getPriceDMonthData(List<Map<String, Object>> dataList, boolean percent) {
        DecimalFormat df = new DecimalFormat("#");
        List<String> cols = Arrays.asList(D_MONTH_PRICE);
        Map<String, Object> total = Maps.newLinkedHashMap();
        cols.stream().forEach(x-> {
            DoubleSummaryStatistics dss = dataList.stream().map(y-> {
                if (!x.equals(D_MONTH_PRICE[0])) {
                    return y.get(x) == null ? 0:Double.valueOf(y.get(x).toString());
                }
                return 0D;
            }).collect(Collectors.summarizingDouble(v->v));

            // 计算本月新增用户数的和
            if(x.equals(D_MONTH_PRICE[1])) {
                total.put(D_MONTH_PRICE[1], dss.getSum());
            }else if(!x.equals(D_MONTH_PRICE[0])){
                if(percent) {
                    Long count = dataList.stream().filter(z-> z.get(x) != null && Double.valueOf(z.get(x).toString()) > 0D).count();
                    total.put(x, count == 0 ? 0:df.format(dss.getSum()/count));
                }else {
                    total.put(x, dss.getSum());
                }
            }
        });
        total.put(D_MONTH_PRICE[0], "合计:");
        dataList.add(total);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dataList);
        return result;
    }

    /**
     * 获取自然月的率  合计数据为 求平均值
     * @param dataList
     * @param begin
     * @param end
     * @return
     */
    public Map<String, Object> getMonthPercentData(List<DatePeriodKpi> dataList, String begin, String end) {
        // 将原始数据按各月分组到Map
        Map<String, List<DatePeriodKpi>> datas = getNewData(dataList, begin, end);

        // 分组后的map组装数据到Map x为每行的月份
        List<Map<String, Object>> sortBeforeData =  datas.keySet().stream().map(x-> {
            Map<String, Object> tmpMap = Maps.newLinkedHashMap();
            List<DatePeriodKpi> list = datas.get(x);
            tmpMap.put("month", x);
            list.stream().filter(z->!z.getBuyPeriod().equals(x)).forEach(y-> {
                    tmpMap.put(y.getBuyPeriod(), y.getRetention());
            });
            list.stream().filter(z->z.getBuyPeriod().equals(x)).forEach(y-> {
                tmpMap.put("newUsers", y.getKpiValue());
            });
            return tmpMap;
        }).collect(Collectors.toList());

        List<Map<String, Object>> data = sortBeforeData.stream().sorted(Comparator.comparing(KpiMonitorServiceImpl::comparingByMonth)).collect(Collectors.toList());

        // 求合计值
        DoubleSummaryStatistics newUsersTotal = data.stream().map(x->Double.valueOf((String)x.get("newUsers"))).collect(Collectors.summarizingDouble(v->v));
        Map<String, Object> map = Maps.newHashMap();
        map.put("month", "合计：");
        map.put("newUsers", newUsersTotal.getSum());

        List<String> monthPeriod = DateUtil.getMonthBetween(begin, end, "yyyyMM");
        monthPeriod.stream().forEach(t-> {
            DoubleSummaryStatistics tmp = data.stream().map(x->(x.get(t) != null && StringUtils.isNotBlank(x.get(t).toString())) ? Double.valueOf(x.get(t).toString()):0D).collect(Collectors.summarizingDouble(v->v));
            Long count = data.stream().filter(x->x.get(t) != null && Double.valueOf(x.get(t).toString()) > 0D).count();
            map.put(t, count == 0 ? 0 : String.format("%.2f", tmp.getSum()/count));
        });
        data.add(map);
        List<String> cols = Lists.newArrayList();
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        cols.add("month");
        cols.add("newUsers");
        monthPeriod.remove(0);
        cols.addAll(monthPeriod);
        result.put("columns", cols);
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
        List<Map<String, Object>> sortBeforeData =  datas.keySet().stream().map(x-> {
            Map<String, Object> tmpMap = Maps.newLinkedHashMap();
            List<DatePeriodKpi> list = datas.get(x);
            tmpMap.put("month", x);
            list.stream().filter(z->!z.getBuyPeriod().equals(x)).forEach(y-> {
                if(percent) {
                    tmpMap.put(y.getBuyPeriod(), y.getRetention());
                }else {
                    tmpMap.put(y.getBuyPeriod(), y.getKpiValue());
                }
                if(y.getBuyPeriod().equals(x)) {
                    tmpMap.put("newUsers", y.getKpiValue());
                }
            });
            list.stream().filter(z->z.getBuyPeriod().equals(x)).forEach(y-> {
                tmpMap.put("newUsers", y.getKpiValue());
            });
            return tmpMap;
        }).collect(Collectors.toList());

        List<Map<String, Object>> data = sortBeforeData.stream().sorted(Comparator.comparing(KpiMonitorServiceImpl::comparingByMonth)).collect(Collectors.toList());

        // 求合计值
        DoubleSummaryStatistics newUsersTotal = data.stream().map(x->x.get("newUsers") == null ? 0D:Double.valueOf((String)x.get("newUsers"))).collect(Collectors.summarizingDouble(v->v));
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
                    map.put(t, tmp.getSum());
                }
            }
        });
        data.add(map);
        List<String> cols = Lists.newArrayList();
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        cols.add("month");
        cols.add("newUsers");
        monthPeriod.remove(0);
        cols.addAll(monthPeriod);
        result.put("columns", cols);
        return result;
    }

    private static Integer comparingByMonth(Map<String, Object> map){
        return Integer.valueOf(map.get("month").toString());
    }

    public Map<String, Object> getUPriceMonthData(List<DatePeriodKpi> dataList, String begin, String end, boolean percent) {
        DecimalFormat df = new DecimalFormat("#");
        // 将原始数据按各月分组到Map
        Map<String, List<DatePeriodKpi>> datas = getNewData(dataList, begin, end);
        // 分组后的map组装数据到Map
        List<Map<String, Object>> sortBeforeData =  datas.keySet().stream().map(x-> {
            Map<String, Object> tmpMap = Maps.newLinkedHashMap();
            List<DatePeriodKpi> list = datas.get(x);
            tmpMap.put("month", x);
            list.stream().filter(z->!z.getBuyPeriod().equals(x)).forEach(y-> {
                if(percent) {
                    tmpMap.put(y.getBuyPeriod(), y.getUprice());
                }else {
                    tmpMap.put(y.getBuyPeriod(), y.getUprice());
                }
            });
            list.stream().filter(z->z.getBuyPeriod().equals(x)).forEach(y-> {
                tmpMap.put("newUsers", y.getKpiValue());
                tmpMap.put("uprice", y.getUprice());
            });
            return tmpMap;
        }).collect(Collectors.toList());

        List<Map<String, Object>> data = sortBeforeData.stream().sorted(Comparator.comparing(KpiMonitorServiceImpl::comparingByMonth)).collect(Collectors.toList());

        // 求合计值
        DoubleSummaryStatistics newUsersTotal = data.stream().map(x->Double.valueOf((String)x.get("newUsers"))).collect(Collectors.summarizingDouble(v->v));
        DoubleSummaryStatistics priceTotal = data.stream().map(x->x.get("uprice") == null ? 0:Double.valueOf((String)x.get("uprice"))).collect(Collectors.summarizingDouble(v->v));
        Map<String, Object> map = Maps.newHashMap();
        map.put("month", "合计：");
        map.put("newUsers", newUsersTotal.getSum());

        Long priceCount = data.stream().filter(x->x.get("uprice") != null && Double.valueOf(x.get("uprice").toString()) > 0D).count();
        map.put("uprice", df.format(priceTotal.getSum()/priceCount));

        List<String> monthPeriod = DateUtil.getMonthBetween(begin, end, "yyyyMM");
        monthPeriod.stream().forEach(t-> {
            DoubleSummaryStatistics tmp = data.stream().map(x->(x.get(t) != null && StringUtils.isNotBlank(x.get(t).toString())) ? Double.valueOf(x.get(t).toString()):0D).collect(Collectors.summarizingDouble(v->v));
            if(percent) {
                Long count = data.stream().filter(x->x.get(t) != null && Double.valueOf(x.get(t).toString()) > 0D).count();
                map.put(t, count == 0 ? 0 : df.format(tmp.getSum()/count));
            }else {
                if (tmp.getSum() != 0D) {
                    map.put(t, tmp.getSum());
                }
            }
        });
        data.add(map);
        List<String> cols = Lists.newArrayList();
        cols.addAll(Arrays.asList("month", "newUsers", "uprice"));
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        monthPeriod.remove(0);
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
    private Map<String, List<DatePeriodKpi>> getNewData(List<DatePeriodKpi> data, String beginDt, String endDt) {
        //起始和结束之间所有的月份
        List<String> monthPeriod = DateUtil.getMonthBetween(beginDt, endDt, "yyyyMM");

        //按行分组 每行代表同期群分析表中的一行
        Map<String, List<DatePeriodKpi>> minPeriodKeysMap = data.stream().collect(Collectors.groupingBy(DatePeriodKpi::getMinPeriod, LinkedHashMap::new,Collectors.toList()));

        monthPeriod.stream().map(m-> {
            //如果当前月没数据
            if(!minPeriodKeysMap.keySet().contains(m)) {

                List<DatePeriodKpi> tmpList = monthPeriod.stream().map(n->{
                    if(compareMonth(m,n))
                    {
                        return new DatePeriodKpi(m,n,"0");
                    }else
                    {
                        return new DatePeriodKpi(m,n,"");
                     }
                }).collect(Collectors.toList());

                minPeriodKeysMap.put(m, tmpList);
            }else {
                //当前月有数据
                List<DatePeriodKpi> tmpList = Lists.newArrayList();
                List<String> temp = minPeriodKeysMap.get(m).stream().map(o-> o.getBuyPeriod()).collect(Collectors.toList());
                monthPeriod.stream().forEach(t-> {
                    if(!temp.contains(t)) {
                        if(compareMonth(t,m)) {
                            tmpList.add(new DatePeriodKpi(m, t, "0"));
                        }else
                        {
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
            //每一行数据
            List<DatePeriodKpi> list = minPeriodKeysMap.get(c);

            //获取第一个数据 因为上面已经完成了补0 所以此处不用考虑为空
            String kpiValue=list.stream().filter(a->c.equals(a.getBuyPeriod())).map(x->x.getKpiValue()).findFirst().get();
            double newUserCount=Double.valueOf("".equals(kpiValue)?"0":kpiValue);

            list.stream().forEach(x-> {
                if(!org.springframework.util.StringUtils.isEmpty(x.getKpiValue()))
                {
                    double retention = Double.valueOf(newUserCount==0? 0D:Double.valueOf(x.getKpiValue())/newUserCount*100);
                    x.setRetention(Double.valueOf(String.format("%.2f", retention)));
                }
            });
        });
        return minPeriodKeysMap;
    }

    /**
     * 比较两个YYYYMM格式的月份那个大，如果end比start大，则返回true 否则返回false
     * @param startMonth
     * @param endMonth
     * @return
     */
    private boolean compareMonth(String startMonth,String endMonth)
    {
        return Integer.parseInt(endMonth)>=Integer.parseInt(startMonth);
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


    /**
     * 根据公式和购买次数生成拟合曲线数据
     */
    @Override
    public List<Double> generateFittingData(String spuId,List<Integer> purchTimes)
    {
        List<Double> result=Lists.newArrayList();

        //获取当前SPU的生成的系数和截距  字符串的格式为 a,b,c|f
        String param=kpiMonitorMapper.getCeofBySpu(spuId);

        if(null==param||param.length()==0||!param.contains(",")||!param.contains("|"))
        {
            return result;
        }

        // 第一个值为系数 第二个值为截距
        List<String> paramList=Splitter.on('|').trimResults().omitEmptyStrings().splitToList(param);

        List<String> ceofList=Splitter.on(',').trimResults().omitEmptyStrings().splitToList(paramList.get(0));
        double ceof1=Double.parseDouble(ceofList.get(0));
        double ceof2=Double.parseDouble(ceofList.get(1));
        double ceof3=Double.parseDouble(ceofList.get(2));
        double intercept=Double.parseDouble(paramList.get(1));

        for(int i:purchTimes)
        {
            //获取对应的系数值
            result.add(calculateFormulaValue(i,ceof1,ceof2,ceof3,intercept));
        }
        return result;
    }

    @Override
    public Map<String, Object> getTotalGmv(String startDt, String endDt) {
        String lastYearStartDt = DateUtil.getLastYear(startDt);
        String lastYearEndDt = DateUtil.getLastYear(endDt);

        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");

        lastYearStartDt = lastYearStartDt.replaceAll("-", "");
        lastYearEndDt = lastYearEndDt.replaceAll("-", "");

        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat(",###");
        Double d1 = kpiMonitorMapper.getTotalGmv(startDt, endDt);

        Double d2 = kpiMonitorMapper.getTotalGmv(lastYearStartDt, lastYearEndDt);
        result.put("gmv", d1 == null ? df.format(0) : df.format(d1));
        df = new DecimalFormat("#.00%");
        result.put("yny", d2 == null ? df.format(1) : df.format((d1-d2)/d2));
        return result;
    }

    private double calculateFormulaValue(int x,double ceof1,double ceof2,double ceof3,double intercept)
    {
        double result= ArithUtil.formatDoubleByMode((Math.pow(x,3)*ceof1+Math.pow(x,2)*ceof2+x*ceof3+intercept) * 100,2, RoundingMode.DOWN);
        return result;
    }

    @Override
    public Map<String, Object> getTotalTradeUser(String startDt, String endDt) {
        String lastYearStartDt = DateUtil.getLastYear(startDt);
        String lastYearEndDt = DateUtil.getLastYear(endDt);

        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");

        lastYearStartDt = lastYearStartDt.replaceAll("-", "");
        lastYearEndDt = lastYearEndDt.replaceAll("-", "");

        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat(",###");
        Double d1 = kpiMonitorMapper.getTotalTradeUser(startDt, endDt);

        Double d2 = kpiMonitorMapper.getTotalTradeUser(lastYearStartDt, lastYearEndDt);
        result.put("tradeUser", d1 == null ? df.format(0) : df.format(d1));
        df = new DecimalFormat("#.00%");
        result.put("yny", d2 == null ? df.format(1) : df.format((d1-d2)/d2));
        return result;
    }

    @Override
    public Map<String, Object> getTotalAvgPrice(String startDt, String endDt) {
        String lastYearStartDt = DateUtil.getLastYear(startDt);
        String lastYearEndDt = DateUtil.getLastYear(endDt);

        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");

        lastYearStartDt = lastYearStartDt.replaceAll("-", "");
        lastYearEndDt = lastYearEndDt.replaceAll("-", "");

        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat(",###");
        Double d1 = kpiMonitorMapper.getTotalAvgPrice(startDt, endDt);

        Double d2 = kpiMonitorMapper.getTotalAvgPrice(lastYearStartDt, lastYearEndDt);
        result.put("avgPrice", d1 == null ? df.format(0) : df.format(d1));
        df = new DecimalFormat("#.00%");
        result.put("yny", d2 == null ? df.format(1) : df.format((d1-d2)/d2));
        return result;
    }

    @Override
    public Echart getOrderAvgPrice(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgOrderPrice(startDt, endDt);
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
        legendData.add("首购平均订单价");
        legendData.add("非首购平均订单价");
        legendData.add("整体平均订单价");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均订单价");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均订单价");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均订单价");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均订单价");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgOrderQuantity(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgOrderQuantity(startDt, endDt);
        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("FP_ORDER_QTT")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("RP_ORDER_QTT")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), ((BigDecimal)t.get("ORDER_QTT")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均订单数");
        legendData.add("非首购平均订单数");
        legendData.add("整体平均订单数");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均订单数");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均订单数");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均订单数");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均订单数");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgPiecePrice(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgPiecePrice(startDt, endDt);
        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), t.get("FP_UP")==null?0D:((BigDecimal)t.get("FP_UP")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), t.get("RP_UP")==null?0D:((BigDecimal)t.get("RP_UP")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), t.get("TOTAL_UP")==null?0D:((BigDecimal)t.get("TOTAL_UP")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均件单价");
        legendData.add("非首购平均件单价");
        legendData.add("整体平均件单价");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均件单价");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均件单价");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均件单价");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均件单价");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }

    @Override
    public Echart getAvgJoinRate(String startDt, String endDt) {
        List<String> dayPeriodList= DateUtil.getEveryday(startDt, endDt);
        startDt = startDt.replaceAll("-", "");
        endDt = endDt.replaceAll("-", "");
        List<Map<String, Object>> list = kpiMonitorMapper.getAvgJoinRate(startDt, endDt);
        Map<String, Double> fp = Maps.newHashMap();
        Map<String, Double> rp = Maps.newHashMap();
        Map<String, Double> total = Maps.newHashMap();
        list.stream().forEach(t-> {
            fp.put((String)t.get("PERIOD_NAME"), t.get("FP_JOIN_RATE")==null?0D:((BigDecimal)t.get("FP_JOIN_RATE")).doubleValue());
            rp.put((String)t.get("PERIOD_NAME"), t.get("RP_JOIN_RATE")==null?0D:((BigDecimal)t.get("RP_JOIN_RATE")).doubleValue());
            total.put((String)t.get("PERIOD_NAME"), t.get("JOIN_RATE")==null?0D:((BigDecimal)t.get("JOIN_RATE")).doubleValue());
        });
        Echart echart = new Echart();
        List<String> legendData = Lists.newArrayList();
        legendData.add("首购平均连带率");
        legendData.add("非首购平均连带率");
        legendData.add("整体平均连带率");
        echart.setLegendData(legendData);

        echart.setxAxisData(dayPeriodList);
        echart.setxAxisName("日期");
        echart.setyAxisName("平均连带率");

        List<Map<String, Object>> seriesData = Lists.newArrayList();
        Map<String, Object> tmp = Maps.newHashMap();
        List<Double> fpList = fixData(fp, dayPeriodList);
        List<Double> rpList = fixData(rp, dayPeriodList);
        List<Double> totalList = fixData(total, dayPeriodList);
        tmp.put("name", "首购平均连带率");
        tmp.put("data", fpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "非首购平均连带率");
        tmp.put("data", rpList);
        seriesData.add(tmp);
        tmp = Maps.newHashMap();
        tmp.put("name", "整体平均连带率");
        tmp.put("data", totalList);
        seriesData.add(tmp);
        echart.setSeriesData(seriesData);
        return echart;
    }
}




