package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 */

import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * 件单价
 */
public interface SpriceMapper {

    /**
     *获取件单价的汇总数据
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    Double getSpriceOfDifferPeriod(String joinInfo, String whereInfo);

    /**
     *获取周期内每个明细周期的件单价
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    List<KpiInfoVo> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    /**
     * 获取首购和复购的汇总数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    KpiInfoVo getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);

    /**
     *获取首购或复购的明细数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    List<KpiInfoVo> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

}
