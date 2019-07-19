package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.WeightIndex;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface WeightIndexMapper extends MyMapper<WeightIndex> {

    List<WeightIndex> getWeightIndex(@Param("periodType") String periodType,@Param("kpiCode")  String kpiCode);

    List<WeightIndex>  getWeightIndexByMonth(@Param("month")  String month,@Param("kpiCode")  String kpiCode);

    List<WeightIndex>  getWeightIndexByDay(@Param("startDt")  String startDt,@Param("endDt")  String endDt,@Param("kpiCode")  String kpiCode);
}