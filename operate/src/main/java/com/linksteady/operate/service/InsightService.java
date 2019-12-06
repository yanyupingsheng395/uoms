package com.linksteady.operate.service;

import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.domain.InsightUserCnt;

import java.util.List;
import java.util.Map;

/**
 * 用户成长洞察
 * @author hxcao
 * @date 2019-12-04
 */
public interface InsightService {

    List<InsightUserCnt> findUserCntList(String dateRange);

    List<InsightGrowthPath> findGrowthPathList(int start, int end, String sortColumn, String sortOrder);

    int findGrowthPathListCount();

    int findImportSpuListCount(String spuName, String purchOrder);

    List<InsightImportSpu> findImportSpuList(int start, int end, String spuName, String purchOrder);

    /**
     * 获取四价值的平均值
     * @return
     */
    InsightGrowthPath getGrowthPathAvgValue();

    Map<String, Object> getSpuList(String dateRange);
}
