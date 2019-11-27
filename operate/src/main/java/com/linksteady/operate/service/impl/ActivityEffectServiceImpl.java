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
    public List<ActivityEffect> getEffectMainKpi(String headId, String pushKpi) {
        List<ActivityEffect> activityEffectList = activityEffectMapper.getEffectMainKpi(headId, pushKpi);
        return activityEffectList;
    }

    @Override
    public List<ActivityEffect> getEffectAllKpi(String headId, String pushKpi) {
        return activityEffectMapper.getEffectAllKpi(headId, pushKpi);
    }

    @Override
    public ActivityEffect getEffectInfo(String headId) {
        return activityEffectMapper.getEffectInfo(headId);
    }
}
