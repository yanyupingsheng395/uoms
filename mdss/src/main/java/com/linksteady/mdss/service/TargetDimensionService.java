package com.linksteady.mdss.service;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-22
 */
public interface TargetDimensionService {
    List<Map<String, Object>> getDataList(List<Map<String, Object>> dataList);
    List<Map<String, Object>> getDimensionsById(String id);
}
