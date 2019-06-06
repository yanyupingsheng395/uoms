package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

public interface OpService {

     List<Map<String,Object>> getOpDayList(int startRow, int endRow, String daywid);

     int getOpDayListCount(String daywid);

     Integer getOpDayUserCountInfo(String daywid);

     List<Map<String,Object>> getOpDayDetailList(int startRow, int endRow, String daywid);

     int getOpDayDetailListCount(String daywid);

     List<Map<String,Object>> getOpDayDetailAllList(String daywid);


     List<Map<String,Object>> getPeriodHeaderList(int startRow, int endRow);

     int getPeriodListCount();

     void savePeriodHeaderInfo(String periodName,String startDt,String endDt);

     List<Map<String,Object>> getPeriodUserList(int startRow, int endRow, String headerId);

     int getPeriodUserListCount(String headerId);

     List<Map<String, Object>> getSpuStatis(String touchDt);

     Echart getChartData(String touchDt, String type);

     List<Map<String, Object>> getPeriodSpuStatis(String headerId);

     Echart getPeriodChartData(String headerId, String type);
}
