package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface KpiCacheMapper {

    List<Map<String,String>> getDiagKpis();

    List<Map<String,String>> getDismantKpis(@Param("kpiCode") String kpiCode);
}
