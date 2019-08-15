package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

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
