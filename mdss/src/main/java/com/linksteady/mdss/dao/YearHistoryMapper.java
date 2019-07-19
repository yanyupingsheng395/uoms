package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.YearHistory;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface YearHistoryMapper extends MyMapper<YearHistory> {

    List<YearHistory> getYearHistory(@Param("year") String year);

    Double getGmvByYear(@Param("year") String year);

    List<Map<String, Object>> getMonthGmvByYear(@Param("year") String year);
}