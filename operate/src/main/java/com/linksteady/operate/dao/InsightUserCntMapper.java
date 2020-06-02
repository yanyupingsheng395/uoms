package com.linksteady.operate.dao;

import com.linksteady.operate.domain.InsightUserCnt;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-12-04
 */
public interface InsightUserCntMapper {

    /**
     * 获取用户数列表数据
     * @param dateRange
     * @return
     */
    List<InsightUserCnt> findUserCntList(Long dateRange);
}
