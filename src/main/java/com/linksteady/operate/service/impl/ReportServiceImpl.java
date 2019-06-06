package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.OpMapper;
import com.linksteady.operate.dao.ReportMapper;
import com.linksteady.operate.service.OpService;
import com.linksteady.operate.service.ReportService;
import com.linksteady.operate.vo.Echart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
