package com.linksteady.operate.dao;

import com.linksteady.operate.domain.InsightGrowthPath;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-12-04
 */
public interface InsightGrowthPathMapper {
    /**
     * 成长旅程价值呈现数据
     * @return
     */
    List<InsightGrowthPath> findGrowthPathList(int limit, int offset, String orderSql, String dateRange);

    /**
     * 成长旅程价值呈现数据大小
     * @return
     */
    int findGrowthPathListCount(String dateRange);

    /**
     * 获取成长旅程四价值平均值
     * @return
     */
    InsightGrowthPath getGrowthPathAvgValue();
}