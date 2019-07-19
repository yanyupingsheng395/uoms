package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.Retention;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface RetentionMapper extends MyMapper<Retention> {
    List<Retention> findMonthDataByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
