package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.Retention;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface RetentionMapper extends MyMapper<Retention> {
    List<Retention> findMonthDataByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
