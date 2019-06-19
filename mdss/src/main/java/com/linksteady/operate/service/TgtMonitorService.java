package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.List;

/**
 * Created by hxcao on 2019-05-24
 */
public interface TgtMonitorService {
    List<Echart> getCharts(String targetId, String periodType, String dt);
}
