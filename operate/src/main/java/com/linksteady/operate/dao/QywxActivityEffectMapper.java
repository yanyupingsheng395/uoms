package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityEffect;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-27
 */
public interface QywxActivityEffectMapper {

    /**
     * 获取主要指标
     * @param headId
     * @param kpiType
     * @return
     */
    List<ActivityEffect> getEffectMainKpi(Long headId, String kpiType);

    /**
     * 获取效果头部信息，pus_kpi 为2
     * @param headId
     * @return
     */
    ActivityEffect getEffectInfo(Long headId);

}
