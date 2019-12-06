package com.linksteady.operate.dao;

import com.linksteady.operate.domain.InsightImportSpu;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-12-05
 */
public interface InsightImportSpuMapper {

    /**
     * 获取重要SPU的记录数
     * @param spuName
     * @param purchOrder
     * @return
     */
    int findImportSpuListCount(String spuName, String purchOrder);

    /**
     * 获取重要SPU的记录
     * @param start
     * @param end
     * @param spuName
     * @param purchOrder
     * @return
     */
    List<InsightImportSpu> findImportSpuList(int start, int end, String spuName, String purchOrder);
}
