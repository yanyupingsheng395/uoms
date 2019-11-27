package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityEffect;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-27
 */
public interface ActivityEffectService {

    List<ActivityEffect> getEffectMainKpi(String headId, String pushKpi);
    List<ActivityEffect> getEffectAllKpi(String headId, String pushKpi);
    ActivityEffect getEffectInfo(String headId);
}
