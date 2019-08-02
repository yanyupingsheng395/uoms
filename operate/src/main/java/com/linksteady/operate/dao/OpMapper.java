package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OpMapper {

    List<Map<String, Object>> getPeriodHeaderList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("taskName") String taskName);

    int getPeriodListCount(@Param("taskName") String taskName);

    void savePeriodHeaderInfo(@Param("periodHeaderId") int periodHeaderId,@Param("periodName") String periodName, @Param("startDt") String startDt, @Param("endDt") String endDt);

    int getPeriodPrimaryKey();

    List<Map<String, Object>> getPeriodUserList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("headerId") String headerId);

    int getPeriodUserListCount(@Param(value = "headerId") String headerId);

    List<Map<String, Object>> getSpuStatis(@Param("touchDt") String touchDt);

    List<Map<String, Object>> getChartData(@Param("touchDt") String touchDt, @Param("type") String type);

    List<Map<String, Object>> getPeriodSpuStatis(@Param("headerId") String headerId);

    List<Map<String, Object>> getPeriodChartData(@Param("headerId") String headerId, @Param("type") String type);

    void copyPeriodStatis(@Param("periodId") int periodId);

    void copyPeriodDetail(@Param("periodId") int periodId);

}