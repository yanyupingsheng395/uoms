package com.linksteady.operate.dao;
import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface KeyPointMapper {

    KeyPointMonth getKeyPointMonthData(@Param("month") String month);

    List<Map<String,Object>> getGMVByDay(@Param("month") String month);

    KeyPointYear getKeyPointYearData(@Param("year") String year);

    List<Map<String,Object>> getGMVTrendByMonth(@Param("year") String year);

    List<Map<String,Object>> getGMVCompareByMonth(@Param("year") String year);

    List<Map<String,Object>> getProfitRateByMonth(@Param("year") String year);

    List<Map<String,Object>> getKeypointHint(@Param("periodvalue") String periodvalue);

    List<Map<String,Object>> getKeypointHintByMonth(@Param("periodvalue") String periodvalue);
}
