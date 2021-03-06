package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityEffectMapper;
import com.linksteady.operate.domain.ActivityEffect;
import com.linksteady.operate.service.ActivityEffectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-27
 */
@Service
public class ActivityEffectServiceImpl implements ActivityEffectService {

    @Autowired
    private ActivityEffectMapper activityEffectMapper;

    @Override
    public List<ActivityEffect> getEffectMainKpi(Long headId, String kpiType) {
        List<ActivityEffect> activityEffectList = activityEffectMapper.getEffectMainKpi(headId, kpiType);
        return activityEffectList;
    }

    @Override
    public ActivityEffect getEffectInfo(Long headId) {
        return activityEffectMapper.getEffectInfo(headId);

    }
}
