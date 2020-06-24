package com.linksteady.operate.util;

import com.linksteady.operate.vo.KpiInfoVO;

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
    List<KpiInfoVO> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    /**
     * 获取首购和非首购的数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    List<KpiInfoVO> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

    /**
     * 获取首购和非首购的总数据
     * @param period_name
     * @param joinInfo
     * @param whereInfo
     * @return
     */
    KpiInfoVO getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);
}
