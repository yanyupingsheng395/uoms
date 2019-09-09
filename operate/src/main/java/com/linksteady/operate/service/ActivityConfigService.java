package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityConfig;
import com.linksteady.operate.domain.ActivityProduct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-06
 */
public interface ActivityConfigService {

    void save(ActivityConfig config);

    List<ActivityConfig> getActivityConfigList();

    List<Map<String, Object>> getActivityConfigByType(String type);

    ActivityConfig getActivityConfigById(String id);
}
