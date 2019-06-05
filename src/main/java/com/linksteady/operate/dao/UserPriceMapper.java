package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 */

import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * 用户数
 */
public interface UserPriceMapper {
    Double getUserCntOfDifferPeriod(String startDt, String endDt, String format, String truncFormat);
    List<KpiInfoVo> getDatePeriodData(String start, String end, String truncFormat, String format);
    KpiInfoVo getSpAndFpKpiTotal(String start, String end, String format, String truncFormat);
    List<KpiInfoVo> getSpAndFpKpi(String start, String end, String format, String truncFormat);
    List<KpiInfoVo> getSpOrFpKpiVal(String isFp, String start, String end, String format, String truncFormat);
}
