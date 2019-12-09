package com.linksteady.operate.service;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Ztree;
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

    /**
     * 获取所有有效的spu
     * @return
     */
    List<Ztree> getSpuTree();

    /**
     * 获取所有上架的商品
     * @param spuWid
     * @return
     */
    List<Ztree> getProductTree(String spuWid);

    /**
     * 留存率随购买次数的变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> retentionInPurchaseTimes(String type, String id, String period);

    /**
     * 件单价随购买次数的变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> unitPriceInPurchaseTimes(String type, String id, String period);

    /**
     * 连带率随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> joinRateInPurchaseTimes(String type, String id, String period);

    /**
     * 品类种数随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> categoryInPurchaseTimes(String type, String id, String period);

    /**
     * 时间间隔随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> periodInPurchaseTimes(String type, String id, String period);
}
