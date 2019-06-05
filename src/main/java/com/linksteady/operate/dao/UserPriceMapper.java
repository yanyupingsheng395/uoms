package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 */

import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * 客单价
 */
public interface UserPriceMapper {
    Double getUserPriceOfDifferPeriod(String startDt, String endDt, String format, String truncFormat);
    List<KpiInfoVo> getDatePeriodData(String start, String end, String truncFormat, String format);
    KpiInfoVo getSpAndFpKpiTotal(String start, String end, String format, String truncFormat);
    List<KpiInfoVo> getSpAndFpKpi(String start, String end, String format, String truncFormat);
    List<KpiInfoVo> getSpOrFpKpiValForOld(String start, String end, String format, String truncFormat);
    List<KpiInfoVo> getSpOrFpKpiValForNew(String start, String end, String format, String truncFormat);
}
