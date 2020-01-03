package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyPropertiesMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.RedisMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyPropertiesServiceImpl implements DailyPropertiesService {

    @Autowired
    DailyPropertiesMapper dailyPropertiesMapper;

    @Autowired
    private RedisMessageService redisMessageService;

    @Override
    public DailyProperties getDailyProperties() {
        return dailyPropertiesMapper.getDailyProperties();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void updateProperties(DailyProperties dailyProperties) {
        dailyPropertiesMapper.updateProperties(dailyProperties);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void sendPushSignal(String signal) {
        HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
        heartBeatInfo.setStartOrStop(signal);
        redisMessageService.sendPushSingal(heartBeatInfo);
    }
}
