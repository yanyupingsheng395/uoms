package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityConfigMapper;
import com.linksteady.operate.domain.ActivityConfig;
import com.linksteady.operate.service.ActivityConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return activityConfigMapper.getActivityConfigByType(type);
    }

    @Override
    public ActivityConfig getActivityConfigById(String id) {
        return activityConfigMapper.getActivityConfigById(id);
    }
}
