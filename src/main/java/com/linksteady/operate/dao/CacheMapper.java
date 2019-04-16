package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface CacheMapper {

    List<Map<String,String>> getDiagKpis();

    List<Map<String,String>> getDismantKpis(@Param("kpiCode") String kpiCode);

    List<Map<String,String>> getDiagDims();

    List<Map<String,String>> getReasonDims();

    List<Map<String,String>> getDimValuesBySql(@Param("sqlString") String sqlString);

    List<Map<String,String>> getDimValuesDirect(@Param("dimCode") String dimCode);
}
