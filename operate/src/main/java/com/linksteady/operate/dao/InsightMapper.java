package com.linksteady.operate.dao;

import com.linksteady.common.domain.Ztree;
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
    List<Ztree> getProductTree(@Param("spuWid") String spuWid);

    List<Map<String, Object>> retentionInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> unitPriceInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> joinRateInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> spuCategoryInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> productCategoryInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> periodInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> getSpuConvertRateProducts(String id, String type, String purchOrder);

    List<Map<String, Object>> findSpuByPurchOrder(String purchOrder);

    List<Map<String, Object>> getSpuRelation(String spuId, String purchOrder);

    List<Map<String, Object>> getProductConvertRate(String productId, String spuId, String purchOrder);

    List<Map<String, Object>> getUserGrowthPathWithProduct(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId);

    List<Map<String, Object>> getUserGrowthPathWithSpu(String spuId, String purchOrder);

    List<Map<String, Object>> getGrowthUser(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId, int limit, int offset);

    int getGrowthUserCount(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId);

    List<Map<String, Object>> getPathSpu();

    List<String> getPathPurchOrder(String spuId);

    List<Map<String, Object>> getUserSpu(String userId);

    String getUserBuyOrder(String userId, String spuId);

    /**
     * 获取用户ebpProduct 和 nextEbpProduct
     * @param userId
     * @param spuId
     * @param buyOrder
     * @return
     */
    Map<String, String> getEbpProductIdByUserId(String userId, String spuId, String buyOrder);

    String getLastBuyDt(String spuId, String userId);

    List<Map<String, String>> getUserGrowthPathPointWithSpu(String userId, String spuId);

    List<Map<String, Object>> getUserGrowthPathPointWithProduct(String userId, String spuId);

    Map<String, String> getUserValueWithSpu(String spuId, String userId);

    int getUserValueWithSpuCount(String spuId);

    List<Map<String, Object>> getConvertDate(String userId);

    List<Map<String, Object>> getPushDate(String userId);

    List<Map<String, Object>> getPushAndConvertDate(String userId);
}
