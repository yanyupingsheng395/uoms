package com.linksteady.operate.controller;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.service.InsightService;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 用户成长洞察
 *
 * @author hxcao
 * @date 2019-12-04
 */
@RestController
@RequestMapping("/insight")
public class InsightController {

    @Autowired
    private InsightService insightService;

    /**
     * 获取用户数随购买次数变化数据
     *
     * @param dateRange
     * @return
     */
    @RequestMapping("/findUserCntList")
    public ResponseBo findUserCntList(@RequestParam("dateRange") String dateRange) {
        return ResponseBo.okWithData(null, insightService.findUserCntList(dateRange));
    }

    /**
     * 成长旅程价值呈现
     *
     * @return
     */
    @GetMapping("/findSpuValueList")
    public ResponseBo findSpuValueList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String dateRange = request.getParam().get("dateRange");
        String sortColumn = request.getSort();
        String sortOrder = request.getOrder();
        int count = insightService.findGrowthPathListCount(dateRange);
        List<InsightGrowthPath> dataList = insightService.findGrowthPathList(limit,offset, sortColumn, sortOrder, dateRange);
        InsightGrowthPath avgValue = insightService.getGrowthPathAvgValue();
        dataList.add(avgValue);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取重要SPU指标值
     *
     * @param request
     * @return
     */
    @RequestMapping("/findImportSpuList")
    public ResponseBo findImportSpuList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String spuId = request.getParam().get("spuId");
        String purchOrder = request.getParam().get("purchOrder");
        String dateRange = request.getParam().get("dateRange");
        String sortColumn = request.getSort();
        String sortOrder = request.getOrder();
        int count = insightService.findImportSpuListCount(purchOrder, dateRange);
        List<InsightImportSpu> dataList = insightService.findImportSpuList(limit,offset, spuId, purchOrder, dateRange, sortColumn, sortOrder);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取spu的桑基图
     *
     * @return
     */
    @RequestMapping("/getSpuSnakey")
    public Map<String, Object> getSpuSnakey(String dateRange) {
        return insightService.getSpuList(dateRange);
    }

    /**
     * 获取所有有效的spu
     *
     * @return
     */
    @RequestMapping("/getSpuTree")
    public ResponseBo getSpuTree() {
        return ResponseBo.okWithData(null, insightService.getSpuTree());
    }

    @RequestMapping("/getProductTree")
    public List<Ztree> getProductTree(@RequestParam("spuWid") Long spuWid) {
        return insightService.getProductTree(spuWid);
    }

    /**
     * type:spu / product
     * 留存率随购买次数的变化图
     *
     * @return
     */
    @GetMapping("/retentionInPurchaseTimes")
    public ResponseBo retentionInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) throws Exception{
        Long periodStartDt=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusMonths(period)));
        Map<String, Object> stringObjectMap = insightService.retentionInPurchaseTimes(type, id, period,periodStartDt);
        return ResponseBo.okWithData(null, stringObjectMap);
    }

    @GetMapping("/retentionInPurchaseTimesOfAll")
    public ResponseBo retentionInPurchaseTimesOfAll(@RequestParam("spuId") String spuId) throws Exception{
        Map<String, Object> stringObjectMap = insightService.retentionInPurchaseTimesOfAll(spuId);
        return ResponseBo.okWithData(null, stringObjectMap);
    }

    /**
     * 件单价随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/unitPriceInPurchaseTimes")
    public ResponseBo unitPriceInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) {
        Long periodStartDt=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusMonths(period)));
        return ResponseBo.okWithData(null, insightService.unitPriceInPurchaseTimes(type, id, periodStartDt));
    }

    /**
     * 连带率随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/joinRateInPurchaseTimes")
    public ResponseBo joinRateInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) {
        Long periodStartDt=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusMonths(period)));
        return ResponseBo.okWithData(null, insightService.joinRateInPurchaseTimes(type, id, periodStartDt));
    }

    /**
     * 品类种数随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/categoryInPurchaseTimes")
    public ResponseBo categoryInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) {
        Long periodStartDt=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusMonths(period)));
        return ResponseBo.okWithData(null, insightService.categoryInPurchaseTimes(type, id, periodStartDt));
    }

    /**
     * 时间间隔随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/periodInPurchaseTimes")
    public ResponseBo periodInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) {
        Long periodStartDt=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusMonths(period)));
        return ResponseBo.okWithData(null, insightService.periodInPurchaseTimes(type, id, periodStartDt));
    }

    /**
     * 留存率变化率随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/retentionChangeRateInPurchaseTimes")
    public ResponseBo retentionChangeRateInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) {
        Long periodStartDt=Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusMonths(period)));
        return ResponseBo.okWithData(null, insightService.retentionChangeRateInPurchaseTimes(type, id, period,periodStartDt));
    }

    /**
     * 获取留存率/变化率的拟合值
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getRetentionFitData")
    public ResponseBo getRetentionFitData(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) throws Exception {
        return ResponseBo.okWithData(null, insightService.getRetentionFitData(type, id, period));
    }

    @GetMapping("/getRetentionChangeFitData")
    public ResponseBo getRetentionChangeFitData(@RequestParam("type") String type, @RequestParam("id") Long id, @RequestParam("period") Long period) throws Exception {
        return ResponseBo.okWithData(null, insightService.getRetentionChangeFitData(type, id, period));
    }

    /**
     * 通过购买次序获取spu
     * @return
     */
    @GetMapping("/findSpuByPurchOrder")
    public ResponseBo findSpuByPurchOrder(@RequestParam("purchOrder") Long purchOrder) {
        return ResponseBo.okWithData(null, insightService.findSpuByPurchOrder(purchOrder));
    }

    /**
     * 获取关系柱状图
     * spu:选择要观测的spu
     * purchOrder:购买的次序
     * @return
     */
    @GetMapping("/getSpuRelation")
    public ResponseBo getSpuRelation(@RequestParam("spuId") Long spuId, @RequestParam("purchOrder") Long purchOrder) {
        return ResponseBo.okWithData(null, insightService.getSpuRelation(spuId, purchOrder));
    }

    /**
     * 获取第一个用户数大的product的转化商品
     * @return
     */
    @GetMapping("/getProductConvertRate")
    public ResponseBo getProductConvertRate(@RequestParam("productId") Long productId, @RequestParam("spuId") Long spuId, @RequestParam("purchOrder") Long purchOrder) {
        return ResponseBo.okWithData(null, insightService.getProductConvertRate(productId, spuId, purchOrder));
    }

    /**
     * 获取spu第n-1～第n次购买，用户成长节点
     * @param spuId
     * @param purchOrder
     * @return
     */
    @GetMapping("/getUserGrowthPath")
    public ResponseBo getUserGrowthPath(@RequestParam("spuId") Long spuId, @RequestParam("purchOrder") Long purchOrder, Long ebpProductId, Long nextEbpProductId) {
        return ResponseBo.okWithData(null, insightService.getUserGrowthPath(spuId, purchOrder, ebpProductId, nextEbpProductId));
    }

    /**
     * 获取成长用户
     * @return
     */
    @GetMapping("/getGrowthUser")
    public ResponseBo getGrowthUser(QueryRequest request) {
        Long spuId = Long.parseLong(request.getParam().get("spuId"));
        Long purchOrder = Long.parseLong(request.getParam().get("purchOrder"));
        Long ebpProductId = Long.parseLong(request.getParam().get("ebpProductId"));
        Long nextEbpProductId = Long.parseLong(request.getParam().get("nextEbpProductId"));
        int limit = request.getLimit();
        int offset = request.getOffset();
        List<Map<String, Object>> dataList = insightService.getGrowthUser(spuId, purchOrder, ebpProductId, nextEbpProductId, limit,offset);
        int count = insightService.getGrowthUserCount(spuId, purchOrder, ebpProductId, nextEbpProductId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取转换概率
     * @return
     */
    @GetMapping("/getConvertRateChart")
    public ResponseBo getConvertRateChart(@RequestParam("spuId") Long spuId, @RequestParam("purchOrder") Long purchOrder,Long ebpProductId, Long nextEbpProductId) throws TException {
        return ResponseBo.okWithData(null, insightService.getConvertRateChart(spuId, purchOrder, ebpProductId, nextEbpProductId));
    }

    /**
     * 获取路径上的所有spu
     * @return
     */
    @GetMapping("/getPathSpu")
    public ResponseBo getPathSpu() {
        return ResponseBo.okWithData(null, insightService.getPathSpu());
    }

    @GetMapping("/getPathPurchOrder")
    public ResponseBo getPathPurchOrder(@RequestParam("spuId") Long spuId) {
        return ResponseBo.okWithData(null, insightService.getPathPurchOrder(spuId));
    }

    // 单个用户成长洞察

    /**
     * 获取用户的成长SPU
     * @param userId
     * @return
     */
    @GetMapping("/getUserSpu")
    public ResponseBo getUserSpu(@RequestParam("userId") Long userId) {
        return ResponseBo.okWithData(null, insightService.getUserSpu(userId));
    }

    /**
     * 获取用户某个spu下的购买次序
     * @param userId
     * @param spuId
     * @return
     */
    @GetMapping("/getUserBuyOrder")
    public ResponseBo getUserBuyOrder(@RequestParam("userId") Long userId, @RequestParam("spuId") Long spuId) {
        return ResponseBo.okWithData(null, insightService.getUserBuyOrder(userId, spuId));
    }

    /**
     * 获取最大购买次序和成长速度和成长目标
     * @param userId
     * @param spuId
     * @return
     */
    @GetMapping("/getUserGrowthData")
    public ResponseBo getUserGrowthData(@RequestParam("userId") String userId, @RequestParam("spuId") String spuId) {
        return ResponseBo.okWithData(null, insightService.getUserGrowthData(userId, spuId));
    }

    /**
     * 获取用户
     * @param userId
     * @param spuId
     * @param buyOrder
     * @return
     */
    @GetMapping("/getUserSpuRelation")
    public ResponseBo getUserSpuRelation(@RequestParam("userId") Long userId, @RequestParam("spuId") Long spuId, @RequestParam("buyOrder") Long buyOrder) {
        return ResponseBo.okWithData(null, insightService.getUserSpuRelation(userId, spuId, buyOrder));
    }

    /**
     * 获取用户距每日运营的时间间隔
     * @return
     */
    @RequestMapping("/getUserBuyDual")
    public ResponseBo getUserBuyDual(@RequestParam("taskDt") String taskDt, @RequestParam("spuId") Long spuId, @RequestParam("userId") Long userId) {
        return ResponseBo.okWithData(null, insightService.getUserBuyDual(spuId, userId, taskDt));
    }

    /**
     * 获取用户成长节点
     * 1.rn是最后一次
     * 2.获取RN下的购买时间
     * 3.计算成长节点日期
     */
    @RequestMapping("/getUserGrowthPathPoint")
    public ResponseBo getUserGrowthPathPoint(@RequestParam("userId") Long userId, @RequestParam("spuId") Long spuId) {
        return ResponseBo.okWithData(null, insightService.getUserGrowthPathPoint(userId, spuId));
    }

    /**
     * 用户在成长类目的价值
     * @param userId
     * @param spuId
     * @return
     */
    @RequestMapping("/getUserValueWithSpu")
    public ResponseBo getUserValueWithSpu(@RequestParam("userId") Long userId, @RequestParam("spuId") Long spuId) {
        return ResponseBo.okWithData(null, insightService.getUserValueWithSpu(userId, spuId));
    }

    @RequestMapping("/getUserConvert")
    public ResponseBo getUserConvert(@RequestParam("userId") Long userId) {
        return ResponseBo.okWithData(null, insightService.getUserConvert(userId));
    }
}
