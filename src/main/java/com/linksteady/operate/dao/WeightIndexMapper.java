package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.WeightIndex;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface WeightIndexMapper extends MyMapper<WeightIndex> {

    List<WeightIndex> getWeightIndex(@Param("year") String year,@Param("kpiCode")  String kpiCode);
}