package com.linksteady.operate.dao;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-14
 */
public interface ActivityWeightMapper {

    List<Map<String, Object>> getWeightIdx(String startDt, String endDt);
}
