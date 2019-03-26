package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.YearHistory;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface YearHistoryMapper extends MyMapper<YearHistory> {

    List<YearHistory> getYearHistory(@Param("year") String year);
}