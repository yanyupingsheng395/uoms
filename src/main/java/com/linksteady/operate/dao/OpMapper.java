package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface OpMapper {

    List<Map<String, Object>> getOpDayList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("daywid") String daywid);

    int getOpDayListCount(@Param(value = "daywid") String daywid);

}