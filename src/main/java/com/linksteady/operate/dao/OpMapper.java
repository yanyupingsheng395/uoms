package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OpMapper{

    List<Map<String, Object>> getOpDayList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("daywid") String daywid);

    int getOpDayListCount(@Param(value="daywid") String daywid);

    Integer getOpDayUserCountInfo(@Param(value="daywid") String daywid);

    List<Map<String, Object>> getOpDayDetailList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("daywid") String daywid);

    Integer getOpDayDetailListCount(@Param(value="daywid") String daywid);

    List<Map<String, Object>> getOpDayDetailAllList(@Param("daywid") String daywid);

    List<Map<String, Object>> getPeriodHeaderList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    int getPeriodListCount();

    void savePeriodHeaderInfo(@Param("periodName") String periodName,@Param("startDt") String startDt,@Param("endDt") String  endDt);

    List<Map<String, Object>> getPeriodUserList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("headerId") String headerId);

    int getPeriodUserListCount(@Param(value="headerId") String headerId);


}