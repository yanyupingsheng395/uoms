package com.linksteady.operate.service;

import java.util.List;
import java.util.Map;

public interface OpService {

     List<Map<String,Object>> getOpDayList(int startRow, int endRow, String daywid);

     int getOpDayListCount(String daywid);

}
