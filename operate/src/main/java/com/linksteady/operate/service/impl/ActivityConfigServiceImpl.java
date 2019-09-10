package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityConfigMapper;
import com.linksteady.operate.domain.ActivityConfig;
import com.linksteady.operate.service.ActivityConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-06
 */
@Service
public class ActivityConfigServiceImpl implements ActivityConfigService {

    @Autowired
    private ActivityConfigMapper activityConfigMapper;



    @Override
    public void save(ActivityConfig config) {
        activityConfigMapper.save(config);
    }

    @Override
    public List<ActivityConfig> getActivityConfigList() {
        return activityConfigMapper.getActivityConfigList();
    }

    @Override
    public List<Map<String, Object>> getActivityConfigByType(String type) {
        LocalDate now = LocalDate.now();
        String currentDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String next2Month = now.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return activityConfigMapper.getActivityConfigByType(type, currentDay, next2Month);
    }

    @Override
    public ActivityConfig getActivityConfigById(String id) {
        return activityConfigMapper.getActivityConfigById(id);
    }

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        String currentDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String next2Month = now.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(next2Month);
    }
}
