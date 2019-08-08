package com.linksteady.operate.dao;

import com.linksteady.operate.domain.KpiSumeryInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface KpiMonitorMapper {

    List<Map<String, Object>> getGMVBySpu(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);

    List<Map<String, Object>> getGMV(@Param("startDt") String startDt, @Param("endDt") String endDt);

    List<Map<String, Object>> getTradeUserBySpu(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);

    List<Map<String, Object>> getTradeUser(@Param("startDt") String startDt, @Param("endDt") String endDt);

    List<Map<String, Object>> getAvgCsPriceBySpu(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);

    List<Map<String, Object>> getAvgCsPrice(@Param("startDt") String startDt, @Param("endDt") String endDt);

    Double getTotalGmv(@Param("startDt") String startDt, @Param("endDt") String endDt);

    Double getTotalTradeUser(@Param("startDt") String startDt, @Param("endDt") String endDt);

    Double getTotalAvgPrice(@Param("startDt") String startDt, @Param("endDt") String endDt);

    List<Map<String, Object>> getAvgOrderPrice(@Param("startDt") String startDt, @Param("endDt") String endDt);

    List<Map<String, Object>> getAvgOrderQuantity(@Param("startDt") String startDt, @Param("endDt") String endDt);

    List<Map<String, Object>> getAvgPiecePrice(@Param("startDt") String startDt, @Param("endDt") String endDt);

    List<Map<String, Object>> getAvgJoinRate(@Param("startDt") String startDt, @Param("endDt") String endDt);

    KpiSumeryInfo getSummaryKpiInfo(@Param("joinInfo") String joinInfo, @Param("whereInfo") String whereInfo);
}