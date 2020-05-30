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

    List<InsightGrowthPath> findGrowthPathList(int limit, int offset, String sortColumn, String sortOrder, String dateRange);

    int findGrowthPathListCount(String dateRange);

    int findImportSpuListCount(String purchOrder, String dateRange);

    List<InsightImportSpu> findImportSpuList(int limit, int offset, String spuId, String purchOrder, String dateRange, String sortColumn, String sortOrder);

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
    List<Ztree> getProductTree(Long spuWid);

    /**
     * 留存率随购买次数的变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    Map<String, Object> retentionInPurchaseTimes(String type, Long id, Long period,Long periodStartDt) throws Exception;

    Map<String, Object> retentionInPurchaseTimesOfAll(String spuId) throws Exception;

    /**
     * 件单价随购买次数的变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    Map<String, Object> unitPriceInPurchaseTimes(String type, Long id, Long periodStartDt);

    /**
     * 连带率随购买次数变化
     *
     * @param type 观测类型 spu or product
     * @param id  对应观测类型的ID
     * @param periodStartDt 观测周期的开始时间
     * @return
     */
    Map<String, Object> joinRateInPurchaseTimes(String type, Long id, Long  periodStartDt);

    /**
     * 品类种数随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    Map<String, Object> categoryInPurchaseTimes(String type, Long id, Long  periodStartDt);

    /**
     * 时间间隔随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    Map<String, Object> periodInPurchaseTimes(String type, Long id, Long periodStartDt);

    /**
     * 留存率变化率随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    Map<String, Object> retentionChangeRateInPurchaseTimes(String type, Long id, Long period,Long periodStartDt);


    /**
     * 获取购买次序下的spu
     *
     * @param purchOrder
     * @return
     */
    List<Map<String, Object>> findSpuByPurchOrder(Long purchOrder);

    /**
     * 获取spu关系柱状图
     *
     * @param spuId
     * @param purchOrder
     * @return
     */
    Map<String, Object> getSpuRelation(Long spuId, Long purchOrder);

    Map<String, Object> getProductConvertRate(Long productId, Long spuId, Long purchOrder);

    List<Map<String, Object>> getUserGrowthPath(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId);

    List<Map<String, Object>> getGrowthUser(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId, int limit, int offset);

    int getGrowthUserCount(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId);

    List<Map<String, Object>> getPathSpu();

    List<String> getPathPurchOrder(Long spuId);

    List<String> getRetentionFitData(String type, Long id, Long period) throws TTransportException;

    List<String> getRetentionChangeFitData(String type, Long id, Long period);

    Map<String, Object> getConvertRateChart(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId);

    List<Map<String, Object>> getUserSpu(Long userId);

    String getUserBuyOrder(Long userId, Long spuId);

    Map<String, Object> getUserSpuRelation(Long userId, Long spuId, Long buyOrder);

    long getUserBuyDual(Long spuId, Long userId, String taskDt);

    List<Map<String, String>> getUserGrowthPathPoint(Long userId, Long spuId);

    Map<String, Object> getUserValueWithSpu(Long userId, Long spuId);

    Map<String, Object> getUserConvert(Long spuId);

    Map<String, String> getUserGrowthData(String userId, String spuId);
}
