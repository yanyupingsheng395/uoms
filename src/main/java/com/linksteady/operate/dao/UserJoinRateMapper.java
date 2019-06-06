package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 */

import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * 连带率
 */
public interface UserJoinRateMapper {

    /**
     *获取连带率的汇总数据
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    Double getUserJoinRateOfDifferPeriod(String joinInfo, String whereInfo);

    /**
     *
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

//    /**
//     *获取首购或复购的明细数据
//     * @param isFp
//     * @param start
//     * @param end
//     * @param format
//     * @param truncFormat
//     * @return
//     */
//    List<KpiInfoVo> getSpOrFpKpiVal(String isFp, String start, String end, String format, String truncFormat);
}
