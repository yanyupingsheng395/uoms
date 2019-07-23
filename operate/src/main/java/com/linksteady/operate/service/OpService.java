package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

public interface OpService {

     List<Map<String,Object>> getOpDayList(int startRow, int endRow, String daywid);

     int getOpDayListCount(String daywid);

     int getOpDayListCount(String daywid, String userActiv, String userValue);

     Integer getOpDayUserCountInfo(String daywid);

     List<Map<String,Object>> getOpDayDetailList(int startRow, int endRow, String daywid);

     int getOpDayDetailListCount(String daywid);

     List<Map<String,Object>> getOpDayDetailAllList(String daywid, String userActiv, String userValue, int start, int end, String sortColumn, String sortOrder);

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
