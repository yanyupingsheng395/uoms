package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DatePeriodKpi;
import com.linksteady.operate.domain.WeekInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface KpiMonitorMapper {
    List<WeekInfo> getWeekList(@Param("beginWid") int beginWid, @Param("endWid") int endWid);
    List<Map<String, Object>> getGMV(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);
    List<Map<String, Object>> getTradeUser(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);
    List<Map<String, Object>> getAvgCsPrice(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);
    List<Map<String, Object>> getRetainDMonth(@Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getRetainBySpuDMonth(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<DatePeriodKpi> getRetainMonth(@Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<DatePeriodKpi> getRetainMonthBySpu(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<DatePeriodKpi> getRetainBySpuMonth(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getRetainUserCountDMonth(@Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getRetainUserCountDMonthBySpu(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getLossUserRateDMonth(@Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getLossUserRateDMonthBySpu(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<DatePeriodKpi> getLossUserMonth(@Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<DatePeriodKpi> getLossUserMonthBySpu(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getLossUserDMonth(@Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getLossUserDMonthBySpu(@Param("spuId") String spuId, @Param("beginDt") String beginDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getUpriceData(@Param("start") String start, @Param("end") String end);
}