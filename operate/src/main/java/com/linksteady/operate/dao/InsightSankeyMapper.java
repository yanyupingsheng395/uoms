package com.linksteady.operate.dao;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-12-02
 */
public interface InsightSankeyMapper {

    List<Map<String, Object>> getSpuList(String dateRange);
}
