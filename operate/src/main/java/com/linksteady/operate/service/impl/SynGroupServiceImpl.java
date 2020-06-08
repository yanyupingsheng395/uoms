package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.util.UomsConstants;
import com.linksteady.operate.dao.SynGroupMapper;
import com.linksteady.operate.domain.DatePeriodKpi;
import com.linksteady.operate.service.SynGroupService;
import com.linksteady.operate.vo.ParamVO;
import com.linksteady.operate.vo.TemplateResult;
import com.ulisesbocchio.jasyptspringboot.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-06-15
 *
 * 同期群数据
 *
 * 如果数据在三角形内，但为NULL（即当月无数据）则置为0
 * 如果数据在三角形外，则置为NULL
 */
@Service
public class SynGroupServiceImpl implements SynGroupService {

    @Autowired
    private SynGroupMapper synGroupMapper;

    /**
     * 品类条件可有可无
     * @param paramVO
     * @param spuId 品类ID
     * @return
     */
    @Override
    public Map<String, Object> getRetentionData(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        String startDt = paramVO.getStartDt();
        String endDt = paramVO.getEndDt();
        String period = paramVO.getPeriodType();
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(period)) {
            List<Map<String, Object>> dataList = synGroupMapper.getRetentionDMonth(paramVO, templateResult);
            return getDMonthData(startDt, endDt, dataList, true, false);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(period)) {
            //自然月数据
            List<DatePeriodKpi> dataList =  synGroupMapper.getRetentionNMonth(paramVO, templateResult);
            return getMonthPercentData(dataList, startDt, endDt);
        }
        return null;
    }

    /**
     * 流失率
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getLossUserRate(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getLossUserRateDMonth(paramVO, templateResult);
            return getDMonthData(paramVO.getStartDt(), paramVO.getEndDt(), dataList, true, false);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList =  synGroupMapper.getLossUserMonth(paramVO, templateResult);
            return getMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), true);
        }
        return null;
    }

    /**
     * 获取流失用户数
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getLossUser(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getLossUserDMonth(paramVO ,templateResult);
            return getDMonthData(paramVO.getStartDt(), paramVO.getEndDt(), dataList, false, false);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList =  synGroupMapper.getLossUserNMonth(paramVO, templateResult);
            return getMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), false);
        }
        return null;
    }

    /**
     * 获取留存用户数
     * @return
     */
    @Override
    public Map<String, Object> getRetainUserCount(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getRetainUserCountDMonth(paramVO, templateResult);
            return getDMonthData(paramVO.getStartDt(), paramVO.getEndDt(), dataList,false, false);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList =  synGroupMapper.getRetainUserCountNMonth(paramVO, templateResult);
            return getMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), false);
        }
        return null;
    }

    /**
     * 获取订单价
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getPriceData(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getPriceDMonth(paramVO, templateResult);
            return getDMonthData( paramVO.getStartDt(), paramVO.getEndDt(), dataList, true, true);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList = synGroupMapper.getPriceNMonth(paramVO, templateResult);
            return getPriceMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), true);
        }
        return null;
    }

    /**
     * 件单价
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getUnitPriceData(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getUnitPriceDMonth(paramVO, templateResult);
            return getDMonthData( paramVO.getStartDt(), paramVO.getEndDt(), dataList, true, true);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList = synGroupMapper.getUnitPriceNMonth(paramVO, templateResult);
            return getPriceMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), true);
        }
        return null;
    }

    /**
     * 连带率
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getJoinRateData(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getJoinRateDMonth(paramVO, templateResult);
            return getDMonthData( paramVO.getStartDt(), paramVO.getEndDt(), dataList, true, true);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList = synGroupMapper.getJoinRateNMonth(paramVO, templateResult);
            return getPriceMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), true);
        }
        return null;
    }

    /**
     * 购买频率
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getPurchFreq(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getPurchFreqDMonth(paramVO, templateResult);
            return getDMonthData( paramVO.getStartDt(), paramVO.getEndDt(), dataList, true, true);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList = synGroupMapper.getPurchFreqNMonth(paramVO, templateResult);
            return getPriceMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), true);
        }
        return null;
    }

    public Map<String, Object> getPriceMonthData(List<DatePeriodKpi> dataList, String begin, String end, boolean percent) {
        DecimalFormat df = new DecimalFormat("#.00");
        // 将原始数据按各月分组到Map
        Map<String, List<DatePeriodKpi>> datas = getNewData(dataList, begin, end);
        // 分组后的map组装数据到Map
        List<Map<String, Object>> sortBeforeData =  datas.keySet().stream().map(x-> {
            Map<String, Object> tmpMap = Maps.newLinkedHashMap();
            List<DatePeriodKpi> list = datas.get(x);
            tmpMap.put("month", x);

            // 购买周期和
            list.stream().filter(z->!z.getBuyPeriod().equals(x) && compareMonth(x, z.getBuyPeriod())).forEach(y-> {
                tmpMap.put(y.getBuyPeriod(), null == y.getUprice() ? 0D:y.getUprice());
            });
            list.stream().filter(z->z.getBuyPeriod().equals(x)).forEach(y-> {
                tmpMap.put("newUsers", y.getKpiValue() == null ? "0":y.getKpiValue());
                tmpMap.put("uprice", y.getUprice() == null ? "0":y.getUprice());
            });
            return tmpMap;
        }).collect(Collectors.toList());

        List<Map<String, Object>> data = sortBeforeData.stream().sorted(Comparator.comparing(SynGroupServiceImpl::comparingByMonth)).collect(Collectors.toList());

        // 求合计值
        // 新增用户数如果没有数据会为空，这里需要做非空的处理,可以在put(newUsers)做添加0的处理
        DoubleSummaryStatistics newUsersTotal = data.stream().map(x-> null == x.get("newUsers") ? 0D : Double.valueOf((String)x.get("newUsers"))).collect(Collectors.summarizingDouble(v->v));
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
     * 订单价
     * @param paramVO
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> getUpriceData(ParamVO paramVO, String spuId) {
        paramVO = resetParam(paramVO);
        TemplateResult templateResult = buildFromAndInfo(spuId);
        if(UomsConstants.PERIOD_TYPE_INTERVAL_MONTH.equals(paramVO.getPeriodType())) {
            List<Map<String, Object>> dataList = synGroupMapper.getUpriceDMonth(paramVO, templateResult);
            return getDMonthData( paramVO.getStartDt(), paramVO.getEndDt(), dataList, true, true);
        }
        if(UomsConstants.PERIOD_TYPE_NOMAL_MONTH.equals(paramVO.getPeriodType())) {
            List<DatePeriodKpi> dataList = synGroupMapper.getUpriceNMonth(paramVO, templateResult);
            return getPriceMonthData(dataList, paramVO.getStartDt(), paramVO.getEndDt(), true);
        }
        return null;
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
                // 新增用户数非空处理，由于下文进行obj to string 类型的转换故为"0"
                tmpMap.put("newUsers", y.getKpiValue() == null ? "0":y.getKpiValue());
            });
            return tmpMap;
        }).collect(Collectors.toList());

        List<Map<String, Object>> data = sortBeforeData.stream().sorted(Comparator.comparing(SynGroupServiceImpl::comparingByMonth)).collect(Collectors.toList());

        // 求合计值
        DoubleSummaryStatistics newUsersTotal = data.stream().map(x->null == x.get("newUsers") ? 0D : Double.valueOf((String)x.get("newUsers"))).collect(Collectors.summarizingDouble(v->v));
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
     * kpiValue为0：代表三角形中的数据，kpiValue为null：代表三角形外的数据
     * @param data
     * @param beginDt yyyyMM
     * @param endDt yyyyMM
     *
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
                    if(compareMonth(n, m))
                    {
                        return new DatePeriodKpi(m,n,null);
                    }else
                    {
                        return new DatePeriodKpi(m,n,"0");
                    }
                }).collect(Collectors.toList());
                minPeriodKeysMap.put(m, tmpList);
            }else {
                //当前月有数据
                List<DatePeriodKpi> tmpList = Lists.newArrayList();
                List<String> buyPeriodList = minPeriodKeysMap.get(m).stream().map(o-> o.getBuyPeriod()).collect(Collectors.toList());
                monthPeriod.stream().forEach(t-> {
                    if(!buyPeriodList.contains(t)) {
                        if(compareMonth(t, m)) {
                            tmpList.add(new DatePeriodKpi(m, t, null));
                        }else
                        {
                            tmpList.add(new DatePeriodKpi(m, t, "0"));
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
            // minPeriod 和 buyPeriod 相同则为当前月的kpi值
            String kpiValue = null;
            // 默认为空，以下表达式排除空的情况
            kpiValue = list.stream().filter(a->c.equals(a.getBuyPeriod()) && a.getKpiValue() != null).map(x->x.getKpiValue()).findFirst().orElse(null);
            // 新增用户数
            Double newUserCount = kpiValue == null ? null : Double.valueOf(kpiValue);
            list.stream().forEach(x-> {
                if(!StringUtils.isEmpty(x.getKpiValue())) {
                    if(newUserCount != null) {
                        double retention = Double.valueOf(newUserCount==0? 0D:Double.valueOf(x.getKpiValue())/newUserCount*100);
                        x.setRetention(Double.valueOf(String.format("%.2f", retention)));
                    } else {
                        x.setRetention(0D);
                    }
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
    private boolean compareMonth(String startMonth,String endMonth) {
        return Integer.parseInt(endMonth)>=Integer.parseInt(startMonth);
    }

    /**
     * 获取日期和cols字段的映射关系
     * @param startDt
     * @param endDt
     * @return
     */
    private Map<String, String> colAndDateMap(String startDt, String endDt) {
        Map<String, String> result = Maps.newHashMap();
        int period = DateUtil.getPeriod(startDt, endDt);
        for (int i=0; i < period; i++) {
            result.put("month" + (i+1), DateUtil.getDateByPlusMonth(startDt, i));
        }
        return result;
    }

    /**
     * 获取间隔月的数据
     * dataList:查询数据
     * isAvg:均值/求和
     * hasCurrentMonthKpi:是否包含当前月指标
     * @return
     */
    public Map<String, Object> getDMonthData(String startDt, String endDt, List<Map<String, Object>> dataList, boolean isAvg, boolean hasCurrentMonthKpi) {
        // 分两种列类型，同期群流失/留存，同期群客单价
        final String[] COL_NAMES = hasCurrentMonthKpi ? UomsConstants.D_MONTH_KPI_COLS : UomsConstants.D_MONTH_COLS;
        List<String> cols = Arrays.asList(COL_NAMES);
        Map<String, Object> total = Maps.newHashMap();
        // 将未来数据置为空
        dataList.stream().forEach(x-> {
            // 当前行的日期
            String startDt0 = String.valueOf(x.get("month_id"));
            Map<String, String> tmpMap = colAndDateMap(startDt0, endDt);
            x.keySet().stream().forEach(y-> {
                List<String> colsList = Arrays.asList(UomsConstants.D_MONTH_COMMONS_COLS);
                if (!colsList.contains(y) && null == tmpMap.get(y)) {
                    x.put(y, null);
                }
                if (null != tmpMap.get(y)) {
                    if(x.get(y) == null) {
                        x.put(y, "0");
                    }
                }
            });
        });

        Map<String, String> colsMap = colAndDateMap(startDt, endDt);
        cols.stream().forEach(x-> {
            DoubleSummaryStatistics dss = dataList.stream().map(y-> {
                if (!x.equals(COL_NAMES[0])) {
                    return y.get(x) == null ? 0:Double.valueOf(y.get(x).toString());
                }
                return 0D;
            }).collect(Collectors.summarizingDouble(v->v));
            if(x.equals(COL_NAMES[1])) {
                /**
                 * 本月新增用户数求和
                 */
                total.put(COL_NAMES[1], dss.getSum());
            }else if(!x.equals(COL_NAMES[0])){
                /**
                 * 剔除第一列'月份'
                 */
                if(isAvg) {
                    /**
                     * 均值
                     */
                    Long count = dataList.stream().filter(z-> z.get(x) != null && Double.valueOf(z.get(x).toString()) > 0D).count();
                    if(colsMap.get(x) != null || x.equals("current_month")) {
                        total.put(x, count == 0 ? 0:String.format("%.2f", dss.getSum()/count));
                    }
                }else {
                    /**
                     * 求和
                     */
                    if(colsMap.get(x) != null) {
                        total.put(x, dss.getSum());
                    }
                }
            }
        });
        total.put(COL_NAMES[0], "合计:");
        dataList.add(total);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dataList);
        return result;
    }

    /**
     * spuid是否为空判断是否跟品类关联
     * @param spuId
     * @return
     */
    private TemplateResult buildFromAndInfo(String spuId) {
        TemplateResult templateResult = new TemplateResult();
        StringBuilder fromInfo = new StringBuilder();
        StringBuilder filterInfo = new StringBuilder();
        StringBuilder fieldInfo = new StringBuilder();

        if(StringUtils.isNotBlank(spuId)) {
            fromInfo.append(" W_ORDER_DETAILS T,W_DATE DT,(SELECT PRODUCT_ID,SPU_WID FROM W_PRODUCT) SPU");
            filterInfo.append(" AND T.PRODUCT_ID = SPU.PRODUCT_ID");
            filterInfo.append(String.format(" AND SPU.SPU_WID = %s", spuId));
            fieldInfo.append(" T.PRICE*T.QUANTITY-T.DISCOUNT REAL_FEE");
        }else {
            fromInfo.append(" W_ORDERS t,W_DATE dt");
            fieldInfo.append(" T.REAL_FEE");
        }
        templateResult.setFilterInfo(String.valueOf(filterInfo));
        templateResult.setFromInfo(String.valueOf(fromInfo));
        templateResult.setFieldInfo(String.valueOf(fieldInfo));

        return templateResult;
    }

    /**
     * 获取结束日期
     * @return
     */
    private ParamVO resetParam(ParamVO paramVO) {
        String startDt = paramVO.getStartDt();
        String endDt = paramVO.getEndDt();
        if(StringUtils.isNotBlank(startDt) && StringUtils.isBlank(endDt)) {
            endDt = DateUtil.getEndDateOfMonth(startDt);
            paramVO.setEndDt(endDt);
        }
        return paramVO;
    }

    private static Integer comparingByMonth(Map<String, Object> map){
        return Integer.valueOf(map.get("month").toString());
    }

    /**
     * 获取自然月的数据
     * @param dataList
     * @param begin
     * @param end
     * @param percent 计算平均数/求和
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
            });
            list.stream().filter(z->z.getBuyPeriod().equals(x)).forEach(y-> {
                tmpMap.put("newUsers", null == y.getKpiValue()?"0":y.getKpiValue());
            });
            return tmpMap;
        }).collect(Collectors.toList());

        List<Map<String, Object>> data = sortBeforeData.stream().sorted(Comparator.comparing(SynGroupServiceImpl::comparingByMonth)).collect(Collectors.toList());

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
                map.put(t, tmp.getSum());
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

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(6,7,9,12,15,20,25,32,41,53,68,88,113,145,186,239,307,394,506,650,835,1073,1378,1771,2275,2922);
        System.out.println(list.stream().collect(Collectors.summarizingInt(x->x)).getSum());
        System.out.println(list.size());
    }
}
