package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyEffect;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-01
 */
public interface DailyEffectService {

    List<DailyEffect> getPageList(int start, int end, String touchDt);

    DailyEffect getKpiStatis(String headId);

    void updateExecuteRate(String headId);
}
