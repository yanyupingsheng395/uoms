package com.linksteady.system.dao;

/**
 * Created by admin
 */

import java.util.List;
import java.util.Map;

public interface ConfigMapper {
    /**
     * 加载其它通用配置
     */
    List<Map<String, String>> selectCommonConfig();

}
