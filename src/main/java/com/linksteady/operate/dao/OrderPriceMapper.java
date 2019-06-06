package com.linksteady.operate.dao;

import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * Created by hxcao on 2019-06-05
 *
 * 订单价
 */
public interface OrderPriceMapper {
    Double getKpiOfDifferPeriod(String joinInfo, String whereInfo);
    List<KpiInfoVo> getDatePeriodData(String period_name, String joinInfo, String whereInfo);
    List<KpiInfoVo> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);
    KpiInfoVo getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);
}
