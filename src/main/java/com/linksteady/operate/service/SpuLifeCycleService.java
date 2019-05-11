package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.List;

/**
 * Created by hxcao on 2019-05-10
 */
public interface SpuLifeCycleService {

    Echart retentionPurchaseTimes(String spuId);
    Echart getPurchDateChart(String spuId, Integer gt, Integer lt) throws Exception;
    Echart getUnitPriceChart(String spuId);
    Echart getDtPeriodChart(String spuId);
    Echart getRateChart(String spuId);
    Echart getCateChart(String spuId);
    Echart getUserCountChart(String spuId);
    Echart getSaleVolumeChart(String spuId);
    List<String> getStageNode(String spuId);
}
