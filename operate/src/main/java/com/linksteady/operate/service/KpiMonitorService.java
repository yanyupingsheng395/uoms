package com.linksteady.operate.service;
import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

public interface KpiMonitorService {
     //     List<WeekInfo> getWeekList(String start, String end);
     Echart getGMV(String startDt, String endDt, String spuId);
     Echart getTradeUser(String startDt, String endDt, String spuId);
     Echart getAvgCsPrice(String startDt, String endDt, String spuId);
     List<Double> generateFittingData(String spuId, List<Integer> purchTimes);
     Map<String, Object> getTotalGmv(String startDt, String endDt);
     Map<String, Object> getTotalTradeUser(String startDt, String endDt);
     Map<String, Object> getTotalAvgPrice(String startDt, String endDt);
     Echart getOrderAvgPrice(String startDt, String endDt);
     Echart getAvgOrderQuantity(String startDt, String endDt);
     Echart getAvgPiecePrice(String startDt, String endDt);
     Echart getAvgJoinRate(String startDt, String endDt);
}
