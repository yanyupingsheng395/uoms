package com.linksteady.operate.service;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.domain.InsightUserCnt;
import org.apache.thrift.transport.TTransportException;

import java.util.List;
import java.util.Map;

/**
 * 用户成长洞察
 *
 * @author hxcao
 * @date 2019-12-04
 */
public interface InsightService {

    List<InsightUserCnt> findUserCntList(String dateRange);

    List<InsightGrowthPath> findGrowthPathList(int start, int end, String sortColumn, String sortOrder, String dateRange);

    int findGrowthPathListCount(String dateRange);

    int findImportSpuListCount(String purchOrder, String dateRange);

    List<InsightImportSpu> findImportSpuList(int start, int end, String spuId, String purchOrder, String dateRange, String sortColumn, String sortOrder);

    /**
     * 获取四价值的平均值
     *
     * @return
     */
    InsightGrowthPath getGrowthPathAvgValue();

    Map<String, Object> getSpuList(String dateRange);

    /**
     * 获取所有有效的spu
     *
     * @return
     */
    List<Ztree> getSpuTree();

    /**
     * 获取所有上架的商品
     *
     * @param spuWid
     * @return
     */
    List<Ztree> getProductTree(String spuWid);

    /**
     * 留存率随购买次数的变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> retentionInPurchaseTimes(String type, String id, String period) throws Exception;

    /**
     * 件单价随购买次数的变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> unitPriceInPurchaseTimes(String type, String id, String period);

    /**
     * 连带率随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> joinRateInPurchaseTimes(String type, String id, String period);

    /**
     * 品类种数随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> categoryInPurchaseTimes(String type, String id, String period);

    /**
     * 时间间隔随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> periodInPurchaseTimes(String type, String id, String period);

    /**
     * 留存率变化率随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> retentionChangeRateInPurchaseTimes(String type, String id, String period);

    /**
     * 获取spu下商品转化关系网
     *
     * @return
     */
    Map<String, Object> getSpuConvertRateNodes(String id, String type, String purchOrder);

    /**
     * 获取购买次序下的spu
     *
     * @param purchOrder
     * @return
     */
    List<Map<String, Object>> findSpuByPurchOrder(String purchOrder);

    /**
     * 获取spu关系柱状图
     *
     * @param spu
     * @param purchOrder
     * @return
     */
    Map<String, Object> getSpuRelation(String spuId, String purchOrder);

    Map<String, Object> getProductConvertRate(String productId, String spuId, String purchOrder);

    List<Map<String, Object>> getUserGrowthPath(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId);

    List<Map<String, Object>> getGrowthUser(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId, int start, int end);

    int getGrowthUserCount(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId);

    List<Map<String, Object>> getPathSpu();

    List<String> getPathPurchOrder(String spuId);

    List<String> getRetentionFitData(String type, String id, String period) throws TTransportException;

    List<String> getRetentionChangeFitData(String type, String id, String period);

    Map<String, Object> getConvertRateChart(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId);

    List<Map<String, Object>> getUserSpu(String userId);

    String getUserBuyOrder(String userId, String spuId);

    Map<String, Object> getUserSpuRelation(String userId, String spuId, String buyOrder);

    long getUserBuyDual(String spuId, String userId, String taskDt);

    List<Map<String, String>> getUserGrowthPathPoint(String userId, String spuId);

    Map<String, Object> getUserValueWithSpu(String userId, String spuId);

    Map<String, Object> getUserConvert(String spuId);
}
