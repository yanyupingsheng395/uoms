package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-03
 */

import java.util.List;
import java.util.Map;

/**
 * 品牌表
 */
public interface BrandMapper {

    List<Map<String, Object>> findAll();
}
