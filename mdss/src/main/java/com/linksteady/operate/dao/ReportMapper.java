package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ReportMapper {

    /**
     * 获取来源的列表
     */
    List<Map<String,String>> getSourceList();

    /**
     * 获取运营日报的数据
     * @return
     */
    LinkedHashMap<String,String> getOpDaillyReport(@Param("sourceType") String sourceType, @Param("startDate") String startDate, @Param("endDate") String endDate,
                                                   @Param("laststartDate") String laststartDate, @Param("lastendDate") String lastendDate);

    List<Map<String,String>> getBrandReportData(@Param("sourceType") String sourceType, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
