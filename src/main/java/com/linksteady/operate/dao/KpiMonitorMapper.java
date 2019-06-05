package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DatePeriodKpi;
import com.linksteady.operate.domain.KpiSumeryInfo;
import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.vo.KpiInfoVo;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface KpiMonitorMapper {
//    List<WeekInfo> getWeekList(@Param("beginWid") int beginWid, @Param("endWid") int endWid);
    List<Map<String, Object>> getGMVBySpu(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);
    List<Map<String, Object>> getGMV(@Param("startDt") String startDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getTradeUserBySpu(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);
    List<Map<String, Object>> getTradeUser(@Param("startDt") String startDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getAvgCsPriceBySpu(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("spuId") String spuId);
    List<Map<String, Object>> getAvgCsPrice(@Param("startDt") String startDt, @Param("endDt") String endDt);
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
    List<DatePeriodKpi> getUpriceDataMonth(@Param("start") String start, @Param("end") String end);
    List<DatePeriodKpi> getUpriceDataMonthBySpu(@Param("spuId") String spuId, @Param("start") String start, @Param("end") String end);
    List<Map<String, Object>> getUpriceDataBySpu(@Param("spuId") String spuId, @Param("start") String start, @Param("end") String end);

    List<Map<String, Object>> getPriceData(@Param("start") String start, @Param("end") String end);
    List<DatePeriodKpi> getPriceDataMonth(@Param("start") String start, @Param("end") String end);
    List<DatePeriodKpi> getPriceDataMonthBySpu(@Param("spuId") String spuId, @Param("start") String start, @Param("end") String end);
    List<Map<String, Object>> getPriceDataBySpu(@Param("spuId") String spuId, @Param("start") String start, @Param("end") String end);
    String getCeofBySpu(@Param("spuId") String spuId);
    Double getTotalGmv(@Param("startDt") String startDt, @Param("endDt") String endDt);
    Double getTotalTradeUser(@Param("startDt") String startDt, @Param("endDt") String endDt);
    Double getTotalAvgPrice(@Param("startDt") String startDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getAvgOrderPrice(@Param("startDt") String startDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getAvgOrderQuantity(@Param("startDt") String startDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getAvgPiecePrice(@Param("startDt") String startDt, @Param("endDt") String endDt);
    List<Map<String, Object>> getAvgJoinRate(@Param("startDt") String startDt, @Param("endDt") String endDt);
    Double getGmvOfDifferPeriod(@Param("startDt") String startDt, @Param("endDt") String endDt, @Param("format") String format, @Param("truncFormat") String truncFormat);
    List<KpiInfoVo> getDatePeriodData(@Param("start") String start, @Param("end") String end, @Param("truncFormat") String truncFormat, @Param("format") String format);
    List<KpiInfoVo> getSpAndFpKpi(@Param("start") String start, @Param("end") String end, @Param("format") String format, @Param("truncFormat") String truncFormat);
    KpiInfoVo getSpAndFpKpiTotal(@Param("start") String start, @Param("end") String end, @Param("format") String format, @Param("truncFormat") String truncFormat);
    List<KpiInfoVo> getSpOrFpKpiVal(@Param("isFp") String isFp, @Param("start") String start, @Param("end") String end, @Param("format") String format, @Param("truncFormat") String truncFormat);

    KpiSumeryInfo getSummaryKpiInfo(@Param("joinInfo") String joinInfo, @Param("whereInfo") String whereInfo);
}