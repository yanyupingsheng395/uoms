package com.linksteady.operate.dao;

import com.linksteady.operate.domain.WeekInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface KpiMonitorMapper {

    List<WeekInfo> getWeekList(@Param("beginWid") int beginWid, @Param("endWid") int endWid);

}