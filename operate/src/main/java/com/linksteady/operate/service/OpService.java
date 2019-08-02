package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

public interface OpService {


     List<Map<String,Object>> getPeriodHeaderList(int startRow, int endRow, String taskName);

     int getPeriodListCount(String taskName);

     void savePeriodHeaderInfo(String periodName, String startDt, String endDt);

     List<Map<String,Object>> getPeriodUserList(int startRow, int endRow, String headerId);

     int getPeriodUserListCount(String headerId);

     List<Map<String, Object>> getSpuStatis(String touchDt);

     Echart getChartData(String touchDt, String type);

     List<Map<String, Object>> getPeriodSpuStatis(String headerId);

     Echart getPeriodChartData(String headerId, String type);
}
