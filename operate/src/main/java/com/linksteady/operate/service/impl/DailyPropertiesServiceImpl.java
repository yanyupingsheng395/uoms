package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyPropertiesMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.DailyPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyPropertiesServiceImpl implements DailyPropertiesService {

    @Autowired
    DailyPropertiesMapper dailyPropertiesMapper;

    @Override
    public DailyProperties getDailyProperties() {
        return dailyPropertiesMapper.getDailyProperties();
    }

    @Override
    public void updateProperties(DailyProperties dailyProperties) {
        dailyPropertiesMapper.updateProperties(dailyProperties);
    }
}
