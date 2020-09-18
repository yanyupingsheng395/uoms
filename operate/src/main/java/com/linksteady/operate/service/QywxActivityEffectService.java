package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityEffect;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-27
 */
public interface QywxActivityEffectService {

    List<ActivityEffect> getEffectMainKpi(Long headId, String kpiType);

    ActivityEffect getEffectInfo(Long headId);
}
