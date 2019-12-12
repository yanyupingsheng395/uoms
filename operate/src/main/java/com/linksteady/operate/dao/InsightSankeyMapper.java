package com.linksteady.operate.dao;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-12-02
 */
public interface InsightSankeyMapper {

    /**
     * 获取桑基图的链接信息
     * @param dateRange
     * @return
     */
    List<Map<String, Object>> getLinkInfo(String dateRange);

    /**
     * 获取source或者target的基本信息，name，id，rn
     * @param dateRange
     * @return
     */
    List<Map<String, Object>> getNodeInfo(String dateRange);
}
