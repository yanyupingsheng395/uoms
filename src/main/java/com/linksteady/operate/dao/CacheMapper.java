package com.linksteady.operate.dao;

import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.KpiConfigInfo;
import com.linksteady.operate.domain.KpiDismantInfo;
import com.linksteady.operate.domain.ReasonTemplateInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface CacheMapper {

    /**
     * 获取所有指标的列表
     * @return
     */
    List<KpiConfigInfo> getKpiList();

    /**
     * 获取指标的拆解信息 (一个指标 可能会有多个拆解公式)
     * @param kpiCode 指标编码
     * @return
     */
    List<KpiDismantInfo> getDismantKpis(@Param("kpiCode") String kpiCode);

    /**
     * 获取诊断的维度信息
     * @return
     */
    List<Map<String,String>> getDiagDims();

    /**
     * 获取原因探究的维度信息
     * @return
     */
    List<Map<String,String>> getReasonDims();

    /**
     * 通过sql的方式获取维度值集合
     * @param sqlString
     * @return
     */
    List<Map<String,Object>> getDimValuesBySql(@Param("sqlString") String sqlString);

    /**
     * 直接查询的方式获取维度值列表
     * @param dimCode
     * @return
     */
    List<Map<String,Object>> getDimValuesDirect(@Param("dimCode") String dimCode);

    /**
     * 获取原因探究的 原因指标列表
     * @return
     */
    List<ReasonTemplateInfo> getReasonRelateKpis();
}
