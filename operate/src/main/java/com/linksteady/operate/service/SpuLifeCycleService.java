package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-10
 */
public interface SpuLifeCycleService {
    String getSampleDate(String spuId);
    Echart retentionPurchaseTimes(String spuId);
    Echart getPurchDateChart(String spuId, String type);
    Echart getUnitPriceChart(String spuId);
    Echart getDtPeriodChart(String spuId);
    Echart getRateChart(String spuId);
    Echart getCateChart(String spuId);
    Echart getUserCountChart(String spuId);
    Echart getSaleVolumeChart(String spuId);
    List<Map<String, Object>> getStageNode(String spuId);
    Echart getStagePeriodData(String spuId, String type);
}
