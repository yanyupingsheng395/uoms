package com.linksteady.mdss.util;

import com.linksteady.mdss.vo.KpiInfoVo;

import java.util.List;

/**
 * @author cao
 *
 * 用户运营监控的mapper
 */
public interface UserOperaterMapper {
    /**
     * 根据join和where条件获取时间段内的KPI值
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    Double getKpiOfDifferPeriod(String joinInfo, String whereInfo);

    /**
     * 获取时间范围内的KPI数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    List<KpiInfoVo> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    /**
     * 获取首购和非首购的数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    List<KpiInfoVo> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

    /**
     * 获取首购和非首购的总数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    KpiInfoVo getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);
}
