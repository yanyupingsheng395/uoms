package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityCovInfo;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/2/26
 */
public interface QywxActivityCovService {

    /**
     * 获取默认的转化值
     * @return
     */
    ActivityCovInfo getConvertInfo(String headId);

    List<ActivityCovInfo> getCovList(String headId, String stage);

    List<Map<String, String>> calculateCov(String headId, String stage, String changedCovId, String defaultCovId);

    void updateCovInfo(long headId, String stage, String covId);

    boolean checkCovInfo(String headId);
}
