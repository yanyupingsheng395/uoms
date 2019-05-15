package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.SpuCycle;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface SpuCycleMapper extends MyMapper<SpuCycle> {
    List<SpuCycle> getDataList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("spuId") String spuId);
    int getTotalCount(@Param("spuId") String spuId);
}