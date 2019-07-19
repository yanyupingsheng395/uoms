package com.linksteady.operate.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-06-03
 */

/**
 * 渠道表
 */
public interface SourceMapper {
    List<Map<String, Object>> findAll();
}
