package com.linksteady.operate.dao;

import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.KpiDismantInfo;
import com.linksteady.operate.domain.ReasonTemplateInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface CacheMapper {

    List<Map<String,String>> getDiagKpis();

    //一个指标 可能会有多个拆解公式
    List<KpiDismantInfo> getDismantKpis(@Param("kpiCode") String kpiCode);

    List<Map<String,String>> getDiagDims();

    List<Map<String,String>> getReasonDims();

    List<Map<String,Object>> getDimValuesBySql(@Param("sqlString") String sqlString);

    List<Map<String,Object>> getDimValuesDirect(@Param("dimCode") String dimCode);

    List<ReasonTemplateInfo> getReasonRelateKpis();
}
