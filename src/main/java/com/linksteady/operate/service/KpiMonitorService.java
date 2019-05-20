package com.linksteady.operate.service;

import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

public interface KpiMonitorService {

//     List<WeekInfo> getWeekList(String start, String end);
     Echart getGMV(String startDt, String endDt, String spuId);
     Echart getTradeUser(String startDt, String endDt, String spuId);
     Echart getAvgCsPrice(String startDt, String endDt, String spuId);
     Map<String, Object> getRetainData(String period, String begin);
     Map<String, Object> getRetentionBySpu(String spuId, String period, String begin);
     Map<String, Object> getRetainUserCount(String period, String begin);
     Map<String, Object> getRetainUserCountBySpu(String spuId, String period, String begin);
     Map<String, Object> getLossUser(String period, String begin);
     Map<String, Object> getLossUserRate(String period, String begin);
     Map<String, Object> getLossUserBySpu(String spuId, String period, String begin);
     Map<String, Object> getLossUserRateBySpu(String spuId, String period, String begin);
     Map<String, Object> getUpriceData(String periodType, String start);
     Map<String, Object> getUpriceDataBySpu(String spuId, String periodType, String start);
     Map<String, Object> getPriceData(String periodType, String start);
     Map<String, Object> getPriceDataBySpu(String spuId, String periodType, String start);
     List<Double> generateFittingData(String spuId,List<Integer> purchTimes);
     Map<String, Object> getTotalGmv(String startDt, String endDt);
     Map<String, Object> getTotalTradeUser(String startDt, String endDt);
     Map<String, Object> getTotalAvgPrice(String startDt, String endDt);
     Echart getOrderAvgPrice(String startDt, String endDt);
     Echart getAvgOrderQuantity(String startDt, String endDt);
     Echart getAvgPiecePrice(String startDt, String endDt);
     Echart getAvgJoinRate(String startDt, String endDt);
}
