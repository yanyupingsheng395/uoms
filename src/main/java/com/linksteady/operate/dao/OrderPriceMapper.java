package com.linksteady.operate.dao;

import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * Created by hxcao on 2019-06-05
 *
 * 订单价
 */
public interface OrderPriceMapper {
    Double getKpiOfDifferPeriod(String startDt, String endDt, String format, String truncFormat);
    List<KpiInfoVo> getDatePeriodData(String start, String end, String truncFormat, String format);
    List<KpiInfoVo> getSpAndFpKpi(String start, String end, String format, String truncFormat);
    KpiInfoVo getSpAndFpKpiTotal(String start, String end, String format, String truncFormat);
    List<KpiInfoVo> getSpOrFpKpiVal(String isFp, String start, String end, String format, String truncFormat);
}
