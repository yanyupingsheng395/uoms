package com.linksteady.mdss.service.impl;

import com.linksteady.mdss.dao.ReportMapper;
import com.linksteady.mdss.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public List<Map<String, String>> getSourceList() {
        return reportMapper.getSourceList();
    }

    @Override
    public LinkedHashMap<String,String> getOpDaillyReport(String sourceType, String startDate, String endDate, String laststartDate, String lastendDate) {
        return reportMapper.getOpDaillyReport(sourceType,startDate,endDate,laststartDate,lastendDate);
    }

    @Override
    public List<Map<String,String>> getBrandReportData(String sourceType, String startDate, String endDate) {
        return reportMapper.getBrandReportData(sourceType,startDate,endDate);
    }
}
