package com.linksteady.operate.service;

import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

public interface KpiMonitorService {

     List<WeekInfo> getWeekList(String start, String end);

     Echart getGMV(String startDt, String endDt, String spuId);

     Echart getTradeUser(String startDt, String endDt, String spuId);

     Echart getAvgCsPrice(String startDt, String endDt, String spuId);
}
