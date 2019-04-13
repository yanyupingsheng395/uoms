package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.YearHistory;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.MapKey;

import java.lang.Double;
import java.util.List;
import java.util.Map;

public interface YearHistoryMapper extends MyMapper<YearHistory> {

    List<YearHistory> getYearHistory(@Param("year") String year);

    Double getGmvByYear(@Param("year") String year);

    List<Map<String, Object>> getMonthGmvByYear(@Param("year") String year);
}