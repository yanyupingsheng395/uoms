package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OpMapper{

    List<Map<String, Object>> getOpDayList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("daywid") String daywid);

    int getOpDayListCount(@Param("daywid") String daywid);

}