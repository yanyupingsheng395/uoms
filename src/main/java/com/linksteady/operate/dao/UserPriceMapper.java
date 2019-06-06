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

    Double getUserPriceOfDifferPeriod(String joinInfo, String whereInfo);

    List<KpiInfoVo> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    KpiInfoVo getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);

    List<KpiInfoVo> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

}
