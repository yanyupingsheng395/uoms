package com.linksteady.operate.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ReportService {

    List<Map<String,String>> getSourceList();

    LinkedHashMap<String,String> getOpDaillyReport(String sourceType, String startDate, String endDate,
                                                   String laststartDate, String lastendDate);

}
