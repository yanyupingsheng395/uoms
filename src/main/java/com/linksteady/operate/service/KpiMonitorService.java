package com.linksteady.operate.service;

import com.linksteady.operate.domain.WeekInfo;

import java.util.List;

public interface KpiMonitorService {

     List<WeekInfo> getWeekList(String start, String end);


}
