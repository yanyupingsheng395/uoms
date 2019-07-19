package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.KeyPointMonth;
import com.linksteady.mdss.domain.KeyPointYear;

import java.util.List;
import java.util.Map;

public interface KeyPointService {

     KeyPointMonth getKeyPointMonthData(String month);

     List<Map<String,Object>> getGMVByDay(String month);

     KeyPointYear getKeyPointYearData(String year);

     List<Map<String,Object>> getGMVTrendByMonth(String year);

     List<Map<String,Object>> getGMVCompareByMonth(String year);

     List<Map<String,Object>> getProfitRateByMonth(String year);

     String isFixProfitByMonth(String month);

     String isFixProfitByYear(String year);

     List<Map<String,Object>> getKeypointHint(String periodtype, String periodvalue);
}
