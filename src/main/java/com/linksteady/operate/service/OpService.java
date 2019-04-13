package com.linksteady.operate.service;

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

}
