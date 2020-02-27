package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityCovInfo;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/2/26
 */
public interface ActivityCovService {

    /**
     * 获取默认的转化值
     * @return
     */
    ActivityCovInfo geConvertInfo(String headId, String stage);

    List<ActivityCovInfo> getCovList();

    void insertCovInfo(String headId, String covListId, String stage);

    List<Map<String, String>> calculateCov(String headId, String stage, String changedCovId, String defaultCovId);

    void updateCovInfo(String headId, String stage, String covId);
}
