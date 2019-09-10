package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityConfig;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-06
 */
public interface ActivityConfigMapper {

    void save(ActivityConfig config);

    List<ActivityConfig> getActivityConfigList();

    List<Map<String, Object>> getActivityConfigByType(String type, String currentDay, String next2Month);

    ActivityConfig getActivityConfigById(String id);
}
