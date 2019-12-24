package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.service.InsightService;
import com.linksteady.operate.thrift.ConversionData;
import com.linksteady.operate.thrift.InsightThriftClient;
import com.linksteady.operate.thrift.RetentionData;
import com.linksteady.operate.util.OkHttpUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 用户成长洞察controller
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
        int start = request.getStart();
        int end = request.getEnd();
        String dateRange = request.getParam().get("dateRange");
        String sortColumn = request.getSort();
        String sortOrder = request.getOrder();
        int count = insightService.findGrowthPathListCount(dateRange);
        List<InsightGrowthPath> dataList = insightService.findGrowthPathList(start, end, sortColumn, sortOrder, dateRange);
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
        int start = request.getStart();
        int end = request.getEnd();
        String spuName = request.getParam().get("spuName");
        String purchOrder = request.getParam().get("purchOrder");
        String dateRange = request.getParam().get("dateRange");
        String sortColumn = request.getSort();
        String sortOrder = request.getOrder();

        int count = insightService.findImportSpuListCount(spuName, purchOrder, dateRange);
        List<InsightImportSpu> dataList = insightService.findImportSpuList(start, end, spuName, purchOrder, dateRange, sortColumn, sortOrder);
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
    public List<Ztree> getProductTree(@RequestParam("spuWid") String spuWid) {
        return insightService.getProductTree(spuWid);
    }

    /**
     * type:spu / product
     * 留存率随购买次数的变化图
     *
     * @return
     */
    @GetMapping("/retentionInPurchaseTimes")
    public ResponseBo retentionInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) throws Exception{
        Map<String, Object> stringObjectMap = insightService.retentionInPurchaseTimes(type, id, period);
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
    public ResponseBo unitPriceInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.unitPriceInPurchaseTimes(type, id, period));
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
    public ResponseBo joinRateInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.joinRateInPurchaseTimes(type, id, period));
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
    public ResponseBo categoryInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.categoryInPurchaseTimes(type, id, period));
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
    public ResponseBo periodInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.periodInPurchaseTimes(type, id, period));
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
    public ResponseBo retentionChangeRateInPurchaseTimes(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.retentionChangeRateInPurchaseTimes(type, id, period));
    }

    /**
     * 获取留存率/变化率的拟合值
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getRetentionFitData")
    public ResponseBo getRetentionFitData(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) throws Exception {
        return ResponseBo.okWithData(null, insightService.getRetentionFitData(type, id, period));
    }

    @GetMapping("/getRetentionChangeFitData")
    public ResponseBo getRetentionChangeFitData(@RequestParam("type") String type, @RequestParam("id") String id, @RequestParam("period") String period) throws Exception {
        return ResponseBo.okWithData(null, insightService.getRetentionChangeFitData(type, id, period));
    }

    /**
     * 获取spu下所有商品下次购买转化的概率的top3的商品->商品关系图
     *
     * @return
     */
    @GetMapping("/getSpuConvertRate")
    public ResponseBo getSpuConvertRateNodes(@RequestParam("id") String id, @RequestParam("type") String type, @RequestParam("purchOrder") String purchOrder) {
        return ResponseBo.okWithData(null, insightService.getSpuConvertRateNodes(id, type, purchOrder));
    }

    /**
     * 通过购买次序获取spu
     * @return
     */
    @GetMapping("/findSpuByPurchOrder")
    public ResponseBo findSpuByPurchOrder(@RequestParam("purchOrder") String purchOrder) {
        return ResponseBo.okWithData(null, insightService.findSpuByPurchOrder(purchOrder));
    }

    /**
     * 获取关系柱状图
     * spu:选择要观测的spu
     * purchOrder:购买的次序
     * @return
     */
    @GetMapping("/getSpuRelation")
    public ResponseBo getSpuRelation(@RequestParam("spuId") String spuId, @RequestParam("purchOrder") String purchOrder) {
        return ResponseBo.okWithData(null, insightService.getSpuRelation(spuId, purchOrder));
    }

    /**
     * 获取第一个用户数大的product的转化商品
     * @return
     */
    @GetMapping("/getProductConvertRate")
    public ResponseBo getProductConvertRate(@RequestParam("productId") String productId, @RequestParam("spuId") String spuId, @RequestParam("purchOrder") String purchOrder) {
        return ResponseBo.okWithData(null, insightService.getProductConvertRate(productId, spuId, purchOrder));
    }

    /**
     * 获取spu第n-1～第n次购买，用户成长节点
     * @param spuId
     * @param purchOrder
     * @return
     */
    @GetMapping("/getUserGrowthPath")
    public ResponseBo getUserGrowthPath(@RequestParam("spuId") String spuId, @RequestParam("purchOrder") String purchOrder, String ebpProductId, String nextEbpProductId) {
        return ResponseBo.okWithData(null, insightService.getUserGrowthPath(spuId, purchOrder, ebpProductId, nextEbpProductId));
    }

    /**
     * 获取成长用户
     * @return
     */
    @GetMapping("/getGrowthUser")
    public ResponseBo getGrowthUser(QueryRequest request) {
        String spuId = request.getParam().get("spuId");
        String purchOrder = request.getParam().get("purchOrder");
        String ebpProductId = request.getParam().get("ebpProductId");
        String nextEbpProductId = request.getParam().get("nextEbpProductId");
        int start = request.getStart();
        int end = request.getEnd();
        List<Map<String, Object>> dataList = insightService.getGrowthUser(spuId, purchOrder, ebpProductId, nextEbpProductId, start, end);
        int count = insightService.getGrowthUserCount(spuId, purchOrder, ebpProductId, nextEbpProductId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取转换概率
     * @return
     */
    @GetMapping("/getConvertRateChart")
    public ResponseBo getConvertRateChart(@RequestParam("spuId") String spuId, @RequestParam("purchOrder") String purchOrder,String ebpProductId, String nextEbpProductId) throws TException {
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
    public ResponseBo getPathPurchOrder(@RequestParam("spuId") String spuId) {
        return ResponseBo.okWithData(null, insightService.getPathPurchOrder(spuId));
    }
}
