package com.linksteady.operate.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.dao.BrandMapper;
import com.linksteady.operate.dao.KpiMonitorMapper;
import com.linksteady.operate.dao.SourceMapper;
import com.linksteady.operate.dao.UserCntMapper;
import com.linksteady.operate.domain.KpiSumeryInfo;
import com.linksteady.operate.service.UserOperatorService;
import com.linksteady.operate.util.DatePeriodUtil;
import com.linksteady.operate.vo.KpiInfoVo;
import com.linksteady.operate.vo.TemplateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-06-03
 */
@Service
public class UserOperatorServiceImpl implements UserOperatorService {

    @Autowired
    private SourceMapper sourceMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private KpiMonitorMapper kpiMonitorMapper;

    @Autowired
    private UserCntMapper userCntMapper;

    @Autowired
    private UserPriceMapper userPriceMapper;

    @Autowired
    private OrderCntMapper orderCntMapper;

    @Autowired
    private OrderPriceMapper orderPriceMapper;

    @Autowired
    private UserJoinRateMapper userJoinRateMapper;

    @Override
    public List<Map<String, Object>> getSource() {
        return sourceMapper.findAll();
    }

    private static final String DEFAULT_VAL = "--";

    /**
     * GMV
     */
    private static final String OP_DATA_GMV = "gmv";

    /**
     * 用户数
     */
    private static final String OP_DATA_USER_CNT = "userCnt";

    /**
     * 客单价
     */
    private static final String OP_DATA_USER_PRICE = "userPrice";

    /**
     * 订单数
     */
    private static final String OP_DATA_ORDER_CNT = "orderCnt";

    /**
     * 订单价
     */
    private static final String OP_DATA_ORDER_PRICE = "orderPrice";

    /**
     *连带率
     */
    private static final String OP_DATA_ORDER_JOINRATE= "jointRate";


    @Override
    public List<Map<String, Object>> getBrand() {
        return brandMapper.findAll();
    }

    /**
     * 计算指标的实际值，去年同期值，上周期值，同环比
     * @param periodType
     * @param startDt
     * @param endDt
     * @return
     */
    @Override
    public Map<String, Object> getKpiInfo(String kpiType, String periodType, String startDt, String endDt,String source) {
        Map<String, Object> result = Maps.newHashMap();
        // 获取去年同期的开始时间和结束时间
        Map<String, Object> date = getLastYearPeriod(periodType, startDt, endDt);

        //去年同期的开始时间和结束时间
        String lastYearStart = (String)date.get("start");
        String lastYearEnd = (String)date.get("end");

        //获取上一周期的开始时间和结束时间
        date = getLastPeriod(periodType, startDt, endDt);

        DecimalFormat df1 = new DecimalFormat(",###.00");
        DecimalFormat df2 = new DecimalFormat("#.00%");

        //获取指标的当前值
        Double d1 = getKpiOfDifferPeriod(kpiType, startDt, endDt, periodType, source);
        Double d2 = getKpiOfDifferPeriod(kpiType, lastYearStart, lastYearEnd, periodType, source);
        // 实际值
        result.put("kpiVal", d1 == null ? DEFAULT_VAL : df1.format(d1));
        // 去年同期值
        result.put("lastYearKpiVal", d2 == null ? DEFAULT_VAL : df1.format(d2));

        //如果为月，需要计算上一周期的值
        if("M".equals(periodType)) {
            String lastStart = (String)date.get("start");
            String lastEnd = (String)date.get("end");
            Double d3 = getKpiOfDifferPeriod(kpiType, lastStart, lastEnd, periodType, source);
            // 上一周期
            result.put("lastKpiVal", d3 == null ? "--" : df1.format(d3));
            result.put("yoy", d1 == null ? DEFAULT_VAL : (d3 == null || d3 == 0D ? DEFAULT_VAL:df2.format((d1 - d3)/d3)));
        }
        // 同比
        result.put("yny", d1 == null ? DEFAULT_VAL : (d2 == null ? DEFAULT_VAL : df2.format((d1-d2)/d2)));
        return result;
    }


    /**
     * 根据传入的周期类型、时间、获取到对应的值
     * @param type  指标类型
     * @param startDt
     * @param endDt
     * @param periodType 周期类型
     * @param source 来源
     * @return
     */
    private Double getKpiOfDifferPeriod(String type, String startDt, String endDt,String periodType,String source) {

        TemplateResult templateResult=buildJoinInfoAndFilter(periodType,startDt,endDt,source);
//
//        if(type.equals(OP_DATA_GMV)) {
//            return kpiMonitorMapper.getGmvOfDifferPeriod(startDt, endDt, format, truncFormat);
//        }
//        if(type.equals(OP_DATA_USER_CNT)) {
//            return  userCntMapper.getUserCntOfDifferPeriod(startDt, endDt, format, truncFormat);
//        }
//        if(type.equals(OP_DATA_USER_PRICE)) {
//            return  userPriceMapper.getUserPriceOfDifferPeriod(startDt, endDt, format, truncFormat);
//        }
//        if(type.equals(OP_DATA_ORDER_CNT)) {
//            return  orderCntMapper.getKpiOfDifferPeriod(startDt, endDt, format, truncFormat);
//        }
//        if(type.equals(OP_DATA_ORDER_PRICE)) {
//            return  orderPriceMapper.getKpiOfDifferPeriod(startDt, endDt, format, truncFormat);
//        }
        if(type.equals(OP_DATA_ORDER_JOINRATE)) {
            return  userJoinRateMapper.getUserJoinRateOfDifferPeriod(templateResult.getJoinInfo(),templateResult.getFilterInfo());
        }
        return null;
    }

    /**
     * 获取本年，去年指标及其均值
     * @param periodType
     * @param startDt
     * @param endDt
     * @return
     */
    @Override
    public Map<String, Object> getKpiChart(String kpiType, String periodType, String startDt, String endDt,String source) {
        Map<String, Object> result = Maps.newHashMap();

        //获取周期内的明细列表
        List<String> dateList = getDateList(periodType, startDt, endDt);
        //周期内的开始日期和结束日期
        String start = dateList.get(0);
        String end = dateList.get(dateList.size()-1);

        //获取周期内的指标值
        List<KpiInfoVo> currentDataList = getDatePeriodData(kpiType, start, end, periodType, source);
        Map<String, Object> currentDataMap = currentDataList.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getKpiVal));
        //修补数据为时间周期上连续，方便echarts展现
        List<Double> currentList = fixData(currentDataMap, dateList);

        String lastStart = String.valueOf(getLastYearPeriod(periodType, startDt, endDt).get("start"));
        String lastEnd = String.valueOf(getLastYearPeriod(periodType, startDt, endDt).get("end"));
        List<String> lastDateList = getDateList(periodType, lastStart, lastEnd);
        start = lastDateList.get(0);
        end = lastDateList.get(dateList.size()-1);
        List<KpiInfoVo> lastDataList = getDatePeriodData(kpiType, start, end, periodType, source);

        Map<String, Object> lastDataMap = lastDataList.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getKpiVal));
        List<Double> lastList = fixData(lastDataMap, dateList);

        Double currentAvg = currentList.stream().mapToDouble(x->x).average().getAsDouble();
        Double lastAvg = lastList.stream().mapToDouble(x->x).average().getAsDouble();

        List<Double> currentAvgList = dateList.stream().map(x->currentAvg).collect(Collectors.toList());
        List<Double> lastAvgList = dateList.stream().map(x->lastAvg).collect(Collectors.toList());
        result.put("xData", dateList);
        result.put("current", currentList);
        result.put("currentAvg", currentAvgList);
        result.put("last", lastList);
        result.put("lastAvg", lastAvgList);
        return result;
    }

    /**
     * 获取时间区间内的KPI值
     * @param kpiType
     * @param startDt
     * @param endDt
     * @param periodType
     * @param source
     * @return
     */
    private List<KpiInfoVo> getDatePeriodData(String kpiType, String startDt, String endDt, String periodType, String source) {
        TemplateResult templateResult=buildJoinInfoAndFilter(periodType,startDt,endDt,source);
        String peroidName=buildPeriodName(periodType);

//        if(kpiType.equals(OP_DATA_GMV)) {
//            return kpiMonitorMapper.getDatePeriodData(start, end, truncFormat, format);
//        }
//        if(kpiType.equals(OP_DATA_USER_CNT)) {
//            return  userCntMapper.getDatePeriodData(start, end, truncFormat, format);
//        }
//        if(kpiType.equals(OP_DATA_USER_PRICE)) {
//            return  userPriceMapper.getDatePeriodData(start, end, truncFormat, format);
//        }
//        if(kpiType.equals(OP_DATA_ORDER_CNT)) {
//            return  orderCntMapper.getDatePeriodData(start, end, truncFormat, format);
//        }
//        if(kpiType.equals(OP_DATA_ORDER_PRICE)) {
//            return  orderPriceMapper.getDatePeriodData(start, end, truncFormat, format);
//        }
        if(kpiType.equals(OP_DATA_ORDER_JOINRATE)) {
            return  userJoinRateMapper.getDatePeriodData(peroidName,templateResult.getJoinInfo(), templateResult.getFilterInfo());
        }
        return null;
    }

    private List<String> getDateList(String periodType, String startDt, String endDt) {
        if("D".equals(periodType)) {
            startDt = startDt + "~" + endDt;
        }
        return DatePeriodUtil.getPeriodDate(periodType, startDt, false);
    }

    /**
     * 获取汇总、首购、复购的按时间周期的明细值
     * @param kpiType
     * @param periodType
     * @param startDt
     * @param endDt
     * @param source
     * @return
     */
    @Override
    public Map<String, Object> getSpAndFpKpi(String kpiType, String periodType, String startDt, String endDt,String source) {
        Map<String, Object> result = Maps.newHashMap();

        List<String> dateList = getDateList(periodType, startDt, endDt);
        String start = dateList.get(0);
        String end = dateList.get(dateList.size()-1);

        //todo ? 周期选的是年，这里传进去的start是月份  应该是传年
        List<KpiInfoVo> kpiInfoVos = getSpAndFpKpiDetail(kpiType, start, end, periodType, source);
        Map<String, Object> kpiValMap = kpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getKpiVal));
        Map<String, Object> fpKpiValMap = kpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getSpKpiVal));
        Map<String, Object> spKpiValMap = kpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getFpKpiVal));
        List<Double> kpiValList = fixData(kpiValMap, dateList);
        List<Double> spKpiValList = fixData(spKpiValMap, dateList);
        List<Double> fpKpiValList = fixData(fpKpiValMap, dateList);

        result.put("xData", dateList);
        result.put("kpiVal", kpiValList);
        result.put("fpKpiVal", fpKpiValList);
        result.put("spKpiVal", spKpiValList);
        return result;
    }

    private List<KpiInfoVo> getSpAndFpKpiDetail(String kpiType, String startDt, String endDt , String periodType, String source) {
        TemplateResult templateResult=buildJoinInfoAndFilter(periodType,startDt,endDt,source);
        String peroidName=buildPeriodName(periodType);
//        if(kpiType.equals(OP_DATA_GMV)) {
//            return kpiMonitorMapper.getSpAndFpKpi(start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_USER_CNT)) {
//            return userCntMapper.getSpAndFpKpi(start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_USER_PRICE)) {
//            return  userPriceMapper.getSpAndFpKpi(start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_ORDER_CNT)) {
//            return  orderCntMapper.getSpAndFpKpi(start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_ORDER_PRICE)) {
//            return  orderPriceMapper.getSpAndFpKpi(start, end, format, truncFormat);
//        }
        if(kpiType.equals(OP_DATA_ORDER_JOINRATE)) {
            return  userJoinRateMapper.getSpAndFpKpi(peroidName,templateResult.getJoinInfo(), templateResult.getFilterInfo());
        }
        return null;
    }

    /**
     * 获取首购的指标值列表及其去年同期、均值
     * @param kpiType
     * @param periodType
     * @param startDt
     * @param endDt
     * @param source
     * @return
     */
    @Override
    public Map<String, Object> getFpKpiVal(String kpiType,String periodType, String startDt, String endDt,String source) {
        Map<String, Object> result = Maps.newHashMap();

        List<String> dateList = getDateList(periodType, startDt, endDt);
        String start = dateList.get(0);
        String end = dateList.get(dateList.size()-1);

        List<KpiInfoVo> kpiInfoVos = getSpOrFpKpiValDetail(kpiType,start, end, periodType, source);

        //获取去年同期的值
        String lastStart = String.valueOf(getLastYearPeriod(periodType, startDt, endDt).get("start"));
        String lastEnd = String.valueOf(getLastYearPeriod(periodType, startDt, endDt).get("end"));
        List<String> lastDateList = getDateList(periodType, lastStart, lastEnd);
        start = lastDateList.get(0);
        end = lastDateList.get(dateList.size()-1);
        List<KpiInfoVo> lastKpiInfoVos = getSpOrFpKpiValDetail(kpiType, start, end, periodType, source);

        Map<String, Object> kpiValMap = kpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getFpKpiVal));
        Map<String, Object> lastKpiValMap = lastKpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getFpKpiVal));
        List<Double> kpiValList = fixData(kpiValMap, dateList);
        List<Double> lastKpiValList = fixData(lastKpiValMap, dateList);

        Double avgKpiVal = kpiValList.stream().mapToDouble(x->x).average().getAsDouble();
        Double avgLastKpiVal = lastKpiValList.stream().mapToDouble(x->x).average().getAsDouble();

        List<Double> avgKpiValList = dateList.stream().map(x->avgKpiVal).collect(Collectors.toList());
        List<Double> lastAvgKpiValList = dateList.stream().map(x->avgLastKpiVal).collect(Collectors.toList());
        result.put("xData", dateList);
        result.put("kpiVal", kpiValList);
        result.put("lastKpiVal", lastKpiValList);
        result.put("avgKpiVal", avgKpiValList);
        result.put("avgLastKpiVal", lastAvgKpiValList);

        return result;
    }

    /**
     * 获取复购的指标值列表及其去年同期、均值
     * @param kpiType
     * @param periodType
     * @param startDt
     * @param endDt
     * @param source
     * @return
     */
    @Override
    public Map<String, Object> getSpKpiVal(String kpiType,String periodType, String startDt, String endDt,String source) {
        Map<String, Object> result = Maps.newHashMap();

        List<String> dateList = getDateList(periodType, startDt, endDt);
        String start = dateList.get(0);
        String end = dateList.get(dateList.size()-1);
        //获取指标值 如果isFp='Y' 则取fpKpiVal 否则取spKpiVal
        List<KpiInfoVo> kpiInfoVos = getSpOrFpKpiValDetail(kpiType,start, end, periodType, source);

        //获取去年同期的值
        String lastStart = String.valueOf(getLastYearPeriod(periodType, startDt, endDt).get("start"));
        String lastEnd = String.valueOf(getLastYearPeriod(periodType, startDt, endDt).get("end"));
        List<String> lastDateList = getDateList(periodType, lastStart, lastEnd);
        start = lastDateList.get(0);
        end = lastDateList.get(dateList.size()-1);
        List<KpiInfoVo> lastKpiInfoVos = getSpOrFpKpiValDetail(kpiType,start, end, periodType, source);

        Map<String, Object> kpiValMap = kpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getSpKpiVal));
        Map<String, Object> lastKpiValMap = lastKpiInfoVos.stream().collect(Collectors.toMap(KpiInfoVo::getKpiDate, KpiInfoVo::getSpKpiVal));
        List<Double> kpiValList = fixData(kpiValMap, dateList);
        List<Double> lastKpiValList = fixData(lastKpiValMap, dateList);

        Double avgKpiVal = kpiValList.stream().mapToDouble(x->x).average().getAsDouble();
        Double avgLastKpiVal = lastKpiValList.stream().mapToDouble(x->x).average().getAsDouble();

        List<Double> avgKpiValList = dateList.stream().map(x->avgKpiVal).collect(Collectors.toList());
        List<Double> lastAvgKpiValList = dateList.stream().map(x->avgLastKpiVal).collect(Collectors.toList());
        result.put("xData", dateList);
        result.put("kpiVal", kpiValList);
        result.put("lastKpiVal", lastKpiValList);
        result.put("avgKpiVal", avgKpiValList);
        result.put("avgLastKpiVal", lastAvgKpiValList);

        return result;
    }

    private List<KpiInfoVo> getSpOrFpKpiValDetail(String kpiType,String startDt, String endDt, String periodType, String source) {
        TemplateResult templateResult=buildJoinInfoAndFilter(periodType,startDt,endDt,source);
        String peroidName=buildPeriodName(periodType);

//        if(kpiType.equals(OP_DATA_GMV)) {
//            return kpiMonitorMapper.getSpOrFpKpiVal(isFp, start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_USER_CNT)) {
//            return userCntMapper.getSpOrFpKpiVal(isFp, start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_USER_PRICE)) {
//            if(isFp.equals("Y")) {
//                return  userPriceMapper.getSpOrFpKpiValForNew(start, end, format, truncFormat);
//            }else {
//                return  userPriceMapper.getSpOrFpKpiValForOld(start, end, format, truncFormat);
//            }
//        }
//        if(kpiType.equals(OP_DATA_ORDER_CNT)) {
//            return  orderCntMapper.getSpOrFpKpiVal(isFp, start, end, format, truncFormat);
//        }
//        if(kpiType.equals(OP_DATA_ORDER_PRICE)) {
//            return  orderPriceMapper.getSpOrFpKpiVal(isFp, start, end, format, truncFormat);
//        }
        if(kpiType.equals(OP_DATA_ORDER_JOINRATE)) {
            return  userJoinRateMapper.getSpAndFpKpi(peroidName,templateResult.getJoinInfo(), templateResult.getFilterInfo());
        }
        return null;
    }

    /**
     * 同比增长率=（本期数－同期数）/ 同期数 × 100%
     * 环比增长率=（本期数-上期数）/ 上期数 × 100%
     * @param periodType
     * @param startDt
     * @param endDt
     * @return
     */
    @Override
    public Map<String, Object> getKpiCalInfo(String kpiType, String periodType, String startDt, String endDt) {
        Map<String, Object> result = Maps.newHashMap();
        String format = "", truncFormat = "";
        switch (periodType){
            case "Y":
                format = "yyyy";
                truncFormat = "yy";
                break;
            case "M":
                format = "yyyymm";
                truncFormat = "mm";
                break;
            case "D":
                format = "yyyymmdd";
                truncFormat = "dd";
                break;
        }

        // 计算当前时间GMV值
        String newStartDt = startDt.replaceAll("-", "");
        String newEndDt = "".equals(endDt) ? newStartDt : endDt.replaceAll("-", "");
        KpiInfoVo kpiInfoVos = getSpAndFpKpiTotal(kpiType, newStartDt, newEndDt, format, truncFormat);

        // 计算上一周期的GMV值
        Map<String, Object> lastDateMap = getLastPeriod(periodType, startDt, endDt);
        String lastStart = String.valueOf(lastDateMap.get("start"));
        String lastEnd = String.valueOf(lastDateMap.get("end"));
        KpiInfoVo lastKpiInfoVos = getSpAndFpKpiTotal(kpiType, lastStart, lastEnd, format, truncFormat);

        // 计算去年的GMV值
        Map<String, Object> lastYearMap = getLastPeriod(periodType, startDt, endDt);
        String lastYearStart = String.valueOf(lastYearMap.get("start"));
        String lastYearEnd = String.valueOf(lastYearMap.get("end"));
        KpiInfoVo lastYearKpiInfoVos = getSpAndFpKpiTotal(kpiType, lastYearStart, lastYearEnd, format, truncFormat);

        DecimalFormat decimalFormat = new DecimalFormat(".##%");
        if(periodType.equals("Y") || periodType.equals("M")) {
            if(kpiInfoVos != null && lastYearKpiInfoVos != null) {
                if(kpiInfoVos.getFpKpiVal() != null && lastYearKpiInfoVos.getFpKpiVal() != null) {
                    Double fpTb = (kpiInfoVos.getFpKpiVal() - lastYearKpiInfoVos.getFpKpiVal())/lastYearKpiInfoVos.getFpKpiVal();
                    result.put("fpTb", decimalFormat.format(fpTb));
                }else {
                    result.put("fpTb", DEFAULT_VAL);
                }

                if(kpiInfoVos.getSpKpiVal() != null && lastYearKpiInfoVos.getSpKpiVal() != null) {
                    Double fpTb = (kpiInfoVos.getSpKpiVal() - lastYearKpiInfoVos.getSpKpiVal())/lastYearKpiInfoVos.getSpKpiVal();
                    result.put("spTb", decimalFormat.format(fpTb));
                }else {
                    result.put("spTb", DEFAULT_VAL);
                }
            }else {
                result.put("fpTb", DEFAULT_VAL);
                result.put("spTb", DEFAULT_VAL);
            }

            if(kpiInfoVos != null && lastKpiInfoVos != null) {
                if(kpiInfoVos.getFpKpiVal() != null && lastKpiInfoVos.getFpKpiVal() != null) {
                    Double fpHb = (kpiInfoVos.getFpKpiVal() - lastKpiInfoVos.getFpKpiVal())/lastKpiInfoVos.getFpKpiVal();
                    result.put("fpHb", fpHb);
                }else {
                    result.put("fpHb", DEFAULT_VAL);
                }

                if(kpiInfoVos.getSpKpiVal() != null && lastKpiInfoVos.getSpKpiVal() != null) {
                    Double fpHb = (kpiInfoVos.getSpKpiVal() - lastKpiInfoVos.getSpKpiVal())/lastKpiInfoVos.getSpKpiVal();
                    result.put("spHb", fpHb);
                }else {
                    result.put("spHb", DEFAULT_VAL);
                }
            }else {
                result.put("fpHb", DEFAULT_VAL);
                result.put("spHb", DEFAULT_VAL);
            }
        }

        String fpAbs = kpiInfoVos == null ? DEFAULT_VAL : (kpiInfoVos.getFpKpiVal() == null ? DEFAULT_VAL : String.valueOf(kpiInfoVos.getFpKpiVal()));
        String spAbs = kpiInfoVos == null ? DEFAULT_VAL : (kpiInfoVos.getSpKpiVal() == null ? DEFAULT_VAL : String.valueOf(kpiInfoVos.getSpKpiVal()));
        String fpContributeRate = kpiInfoVos == null ? DEFAULT_VAL : (kpiInfoVos.getFpKpiVal() == null ? DEFAULT_VAL : (kpiInfoVos.getKpiVal() == null ? DEFAULT_VAL : decimalFormat.format(kpiInfoVos.getFpKpiVal()/kpiInfoVos.getKpiVal())));
        String spContributeRate = kpiInfoVos == null ? DEFAULT_VAL : (kpiInfoVos.getSpKpiVal() == null ? DEFAULT_VAL : (kpiInfoVos.getKpiVal() == null ? DEFAULT_VAL : decimalFormat.format(kpiInfoVos.getSpKpiVal()/kpiInfoVos.getKpiVal())));
        result.put("fpAbs", fpAbs); // 首购绝对值
        result.put("spAbs", spAbs); // 复购绝对值
        result.put("fpContributeRate", fpContributeRate); // 首购贡献率
        result.put("spContributeRate", spContributeRate); // 复购贡献率
        return result;
    }

    private KpiInfoVo getSpAndFpKpiTotal(String kpiType, String start, String end, String format, String truncFormat) {
        if(kpiType.equals(OP_DATA_GMV)) {
            return kpiMonitorMapper.getSpAndFpKpiTotal(start, end, format, truncFormat);
        }
        if(kpiType.equals(OP_DATA_USER_CNT)) {
            return userCntMapper.getSpAndFpKpiTotal(start, end, format, truncFormat);
        }
        if(kpiType.equals(OP_DATA_USER_PRICE)) {
            return  userPriceMapper.getSpAndFpKpiTotal(start, end, format, truncFormat);
        }
        if(kpiType.equals(OP_DATA_ORDER_CNT)) {
            return  orderCntMapper.getSpAndFpKpiTotal(start, end, format, truncFormat);
        }
        if(kpiType.equals(OP_DATA_ORDER_PRICE)) {
            return  orderPriceMapper.getSpAndFpKpiTotal(start, end, format, truncFormat);
        }
        return null;
    }

    private List<Double> fixData(Map<String, Object> datas, List<String> periodList)
    {
        return periodList.stream().map(s->{
            if(null==datas.get(s)||"".equals(datas.get(s)))
            {
                return 0d;
            }else
            {
                return Double.valueOf(datas.get(s).toString());
            }
        }).collect(Collectors.toList());
    }

    /**
     * 获取上一周期时间值
     * @param periodType Y: 年 M：月 D:日
     * @param startDt yyyy-MM-dd
     * @param endDt yyyy-MM-dd
     * @return
     */
    private static Map<String, Object> getLastPeriod(String periodType, String startDt, String endDt) {
        Map<String, Object> result = Maps.newHashMap();
        String lastStartDt = "";
        if(periodType.equals("M")) {
            lastStartDt = DateUtil.getLast(startDt);
        }
        result.put("start", lastStartDt);
        result.put("end", lastStartDt);
        return result;
    }

    /**
     * 获取去年同期的值
     * @param periodType Y: 年 M：月 D:日
     * @param startDt yyyy-MM-dd
     * @param endDt yyyy-MM-dd
     * @return
     */
    private static Map<String, Object> getLastYearPeriod(String periodType, String startDt, String endDt) {
        Map<String, Object> result = Maps.newHashMap();
        String lastStartDt;
        if(periodType.equals("Y")) { // 年
            lastStartDt = String.valueOf((Integer.valueOf(startDt) - 1));
            result.put("start", lastStartDt);
            result.put("end", lastStartDt);
        }
        if(periodType.equals("M")) {
            lastStartDt = DateUtil.getLastYear(startDt);
            result.put("start", lastStartDt);
            result.put("end", lastStartDt);
        }
        if(periodType.equals("D")) {
            result = DateUtil.getLastYearOfDay(startDt, endDt);
        }
        return result;
    }

    /**
     * 获取所有的指标信息
     * @param periodType
     * @param startDt
     * @param endDt
     * @param source
     * @return
     */
    @Override
    public KpiSumeryInfo getSummaryKpiInfo(String periodType, String startDt, String endDt, String source) {

        TemplateResult templateResult=buildJoinInfoAndFilter(periodType,startDt,endDt,source);
        return kpiMonitorMapper.getSummaryKpiInfo(templateResult.getJoinInfo(),templateResult.getFilterInfo());
    }


    /**
     * 构造where 信息
     * @return
     */
    private TemplateResult buildJoinInfoAndFilter(String periodType, String startDt, String endDt,String source)
    {
        Joiner joiner = Joiner.on(",").skipNulls();
        //构造where条件
        StringBuffer bf=new StringBuffer();
        StringBuffer join=new StringBuffer();

        //处理条件 Y M D
        if("D".equals(periodType))
        {
            bf.append(" AND W_DATE.DAY>=").append(StringUtils.replaceChars(startDt,"-","")).append(" AND W_DATE.DAY<=").append(StringUtils.replaceChars(endDt,"-",""));
        }else if("M".equals(periodType))
        {
            bf.append(" AND W_DATE.MONTH=").append(StringUtils.replaceChars(startDt,"-",""));
        }else if("Y".equals(periodType))
        {
            bf.append(" AND W_DATE.YEAR=").append(startDt);
        }

        if(null!=source&&!"".equals(source))
        {
            join.append(" JOIN  W_SOURCE ON W_ORDERS.SOURCE_ID=W_SOURCE.SOURCE_ID");

            List<String> sourceList=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(source);

            if(sourceList.size()==1)
            {
                bf.append(" AND ").append("W_SOURCE.SOURCE_ID").append("=").append(sourceList.get(0));
            }else
            {
                bf.append(" AND ").append("W_SOURCE.SOURCE_ID").append(" IN(").append(joiner.join(sourceList)).append(")");
            }
        }

        TemplateResult templateResult=new TemplateResult();
        templateResult.setJoinInfo(join.toString());
        templateResult.setFilterInfo(bf.toString());

        return templateResult;
    }

    /**
     * 构造周期类型名称
     */
    private String buildPeriodName(String periodType)
    {
        if("D".equals(periodType))
        {
            return " W_DATE.DAY";
        }else if("M".equals(periodType))
        {
            return " W_DATE.DAY";
        }else if("Y".equals(periodType))
        {
            return " W_DATE.MONTH";
        }else
        {
            return "";
        }
    }
}
