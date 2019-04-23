package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.Retention;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface RetentionMapper extends MyMapper<Retention> {
    List<Retention> findMonthDataByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
