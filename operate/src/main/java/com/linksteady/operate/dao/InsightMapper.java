package com.linksteady.operate.dao;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.InsightUserEffect;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-12-07
 */
public interface InsightMapper {

    List<Ztree> getSpuTree();

    /**
     * 获取product树
     * @param spuWid
     * @return
     */
    List<Ztree> getProductTree(@Param("spuWid") long spuWid);

    List<Map<String, Object>> retentionInPurchaseTimes(String type, long id, long periodStartDt);

    List<Map<String, Object>> retentionInPurchaseTimesOfAll(String spuId);

    List<Map<String, Object>> unitPriceInPurchaseTimes(String type, long id, long periodStartDt);

    List<Map<String, Object>> joinRateInPurchaseTimes(String type, long id, long periodStartDt);

    List<Map<String, Object>> spuCategoryInPurchaseTimes(String type, long id, long periodStartDt);

    List<Map<String, Object>> productCategoryInPurchaseTimes(String type, long id, long periodStartDt);

    List<Map<String, Object>> periodInPurchaseTimes(String type, long id, long periodStartDt);

    List<Map<String, Object>> findSpuByPurchOrder(long purchOrder);

    List<Map<String, Object>> getSpuRelation(long spuId, long purchOrder);

    List<Map<String, Object>> getProductConvertRate(long productId, long spuId, long purchOrder);

    List<Map<String, Object>> getUserGrowthPathWithProduct(long spuId, long purchOrder, long ebpProductId, long nextEbpProductId);

    List<Map<String, Object>> getUserGrowthPathWithSpu(long spuId, long purchOrder);

    List<Map<String, Object>> getGrowthUser(long spuId, long purchOrder, long ebpProductId, long nextEbpProductId, int limit, int offset);

    int getGrowthUserCount(long spuId, long purchOrder, long ebpProductId, long nextEbpProductId);

    List<Map<String, Object>> getPathSpu();

    List<String> getPathPurchOrder(long spuId);

    List<Map<String, Object>> getUserSpu(long userId);

    String getUserBuyOrder(long userId, long spuId);

    /**
     * 获取用户ebpProduct 和 nextEbpProduct
     * @param userId
     * @param spuId
     * @param buyOrder
     * @return
     */
    List<Map<String, String>> getEbpProductIdByUserId(long userId, long spuId, long buyOrder);

    String getLastBuyDt(long spuId, long userId);

    List<Map<String, String>> getUserGrowthPathPointWithSpu(long userId, long spuId);

    List<Map<String, Object>> getUserGrowthPathPointWithProduct(long userId, long spuId);

    Map<String, String> getUserValueWithSpu(long spuId, long userId);

    int getUserValueWithSpuCount(long spuId);

    List<Map<String, Object>> getConvertDate(long userId);

    List<Map<String, Object>> getPushDate(long userId);

    List<Map<String, Object>> getPushAndConvertDate(long userId);

    Map<String, String> getGrowthData(String userId, String spuId);

    int getGrowthTableDataCount(String startDt, String endDt);

    List<InsightUserEffect> getGrowthTableDataList(String startDt, String endDt, Integer limit, Integer offset);

    List<Map<String, Object>> allGrowthP(String startDt, String endDt);

    List<Map<String, Object>> allGrowthV(String startDt, String endDt);

    List<Map<String, Object>> singleGrowthP(String userId);

    List<Map<String, Object>> singleGrowthV(String userId);
}
