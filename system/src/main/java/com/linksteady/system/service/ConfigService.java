package com.linksteady.system.service;

import java.util.List;
import java.util.Map;

public interface ConfigService {

    /**
     * 加载其它通用配置
     */
    List<Map<String, String>> selectCommonConfig();

}
