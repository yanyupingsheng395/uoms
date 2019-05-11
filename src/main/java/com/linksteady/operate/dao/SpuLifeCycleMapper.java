package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-10
 */
public interface SpuLifeCycleMapper {
    List<Map<String, Object>> retentionPurchaseTimes(@Param("spuId") String spuId);

    List<Map<String, Object>> getPurchDate(@Param("spuId")String spuId, @Param("gtimes")Integer gtimes, @Param("ltimes")Integer ltimes);

    List<Map<String, Object>> getUnitPriceChart(@Param("spuId") String spuId);

    List<Map<String, Object>> getDtPeriodChart(@Param("spuId") String spuId);

    List<Map<String, Object>> getRateChart(@Param("spuId") String spuId);

    List<Map<String, Object>> getCateChart(@Param("spuId") String spuId);

    List<Map<String, Object>> getLifeCycleKpi(@Param("spuId") String spuId);

    List<String> getStageNode(@Param("spuId") String spuId);
}
