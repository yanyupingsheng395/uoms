package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-03
 */

import java.util.List;
import java.util.Map;

/**
 * 品牌表
 */
public interface ConfigMapper {

    List<Map<String, String>> selectPathActive();

    List<Map<String, String>> selectUserValue();

    List<Map<String, String>> selectLifeCycle();

    /**
     * 加载其它通用配置
     */
    List<Map<String, String>> selectCommonConfig();

}
