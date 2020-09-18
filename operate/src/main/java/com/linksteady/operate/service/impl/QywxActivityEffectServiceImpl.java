package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityEffectMapper;
import com.linksteady.operate.dao.QywxActivityEffectMapper;
import com.linksteady.operate.domain.ActivityEffect;
import com.linksteady.operate.service.ActivityEffectService;
import com.linksteady.operate.service.QywxActivityEffectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-27
 */
@Service
public class QywxActivityEffectServiceImpl implements QywxActivityEffectService {

    @Autowired
    private QywxActivityEffectMapper activityEffectMapper;

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
