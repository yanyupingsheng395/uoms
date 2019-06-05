package com.linksteady.operate.service;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-06-03
 */
public interface UserOperatorService {
    List<Map<String, Object>> getSource();
    List<Map<String, Object>> getBrand();
    Map<String, Object> getKpiInfo(String kpiType, String periodType, String startDt, String endDt);
    Map<String, Object> getKpiChart(String kpiType, String periodType, String startDt, String endDt);
    Map<String, Object> getSpAndFpKpi(String kpiType, String periodType, String startDt, String endDt);
    Map<String, Object> getSpOrFpKpiVal(String kpiType, String isFp, String periodType, String startDt, String endDt);
    Map<String, Object> getKpiCalInfo(String kpiType, String periodType, String startDt, String endDt);
}
