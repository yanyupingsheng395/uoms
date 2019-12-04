package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityEffect;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-27
 */
public interface ActivityEffectMapper {

    /**
     * 获取主要指标
     * @param headId
     * @param pushKpi
     * @return
     */
    List<ActivityEffect> getEffectMainKpi(String headId, String pushKpi);

    /**
     * 获取效果头部信息，pus_kpi 为2
     * @param headId
     * @return
     */
    ActivityEffect getEffectInfo(String headId);

    /**
     * 获取全部指标
     * @param headId
     * @param pushKpi
     * @return
     */
    List<ActivityEffect> getEffectAllKpi(String headId, String pushKpi);
}
