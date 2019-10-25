package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyProperties;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyPropertiesService {

    /**
     * 获取每日成长运营的配置信息
     * @return
     */
    DailyProperties getDailyProperties();


    /**
     * 更新每日成长运营的配置信息
     */
    void updateProperties(DailyProperties dailyProperties);

}
