package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.KpiMonitorMapper;
import com.linksteady.operate.dao.OpMapper;
import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.service.OpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class KpiMonitorServiceImpl implements KpiMonitorService {

    @Autowired
    private KpiMonitorMapper kpiMonitorMapper;


    @Override
    public List<WeekInfo> getWeekList(String start, String end) {
        return kpiMonitorMapper.getWeekList(Integer.parseInt(start.replace("-","")),Integer.parseInt(end.replace("-","")));
    }
}
