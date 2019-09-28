package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyProperties;

public interface DailyPropertiesMapper {

    /**
     * 获取每日成长运营的配置信息
     * @return
     */
    DailyProperties getDailyProperties();

    /**
     * 更新每日用户成长运营的配置信息
     * @param dailyProperties
     */
    void updateProperties(DailyProperties dailyProperties);

}
