package com.linksteady.system.service;

import com.linksteady.common.domain.Tconfig;

import java.util.List;
import java.util.Map;

public interface ConfigService {

    /**
     * 加载其它通用配置
     */
    void loadConfigToRedis();

    /**
     * 根据name获取value的值
     */
    String getValueByName(String name);

    /**
     * 获取所有配置的列表
     */
    List<Tconfig> selectConfigList();

}
