package com.linksteady.operate.dao;

import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.*;
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

    /**
     * 获取KPI的查询模板信息
     */
    List<KpiSqlTemplate> getKpiSqlTemplateList();


    /**
     * 获取所有维度配置信息表的数据
     */
    List<DimConfigInfo> getAllDimConfig();

    /**
     * 获取所有维度的关联信息
     */
    List<DimJoinRelationInfo> getDimJoinRelationList();
}
