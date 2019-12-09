package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * @param dateRange
     * @return
     */
    @RequestMapping("/findUserCntList")
    public ResponseBo findUserCntList(@RequestParam("dateRange") String dateRange) {
        return ResponseBo.okWithData(null, insightService.findUserCntList(dateRange));
    }

    /**
     * 成长旅程价值呈现
     * @return
     */
    @GetMapping("/findSpuValueList")
    public ResponseBo findSpuValueList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        int count = insightService.findGrowthPathListCount();
        String sortColumn = request.getSort();
        String sortOrder = request.getOrder();
        List<InsightGrowthPath> dataList = insightService.findGrowthPathList(start, end, sortColumn, sortOrder);
        InsightGrowthPath avgValue = insightService.getGrowthPathAvgValue();
        dataList.add(avgValue);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取重要SPU指标值
     * @param request
     * @return
     */
    @RequestMapping("/findImportSpuList")
    public ResponseBo findImportSpuList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String spuName = request.getParam().get("spuName");
        String purchOrder = request.getParam().get("purchOrder");
        int count = insightService.findImportSpuListCount(spuName, purchOrder);
        List<InsightImportSpu> dataList = insightService.findImportSpuList(start, end, spuName, purchOrder);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取spu的桑基图
     * @return
     */
    @RequestMapping("/getSpuSnakey")
    public Map<String, Object> getSpuSnakey(String dateRange) {
        return insightService.getSpuList(dateRange);
    }

    /**
     * 获取所有有效的spu
     * @return
     */
    @RequestMapping("/getSpuTree")
    public ResponseBo getSpuTree() {
        return ResponseBo.okWithData(null,insightService.getSpuTree());
    }

    @RequestMapping("/getProductTree")
    public List<Ztree> getProductTree(@RequestParam("spuWid") String spuWid) {
        return insightService.getProductTree(spuWid);
    }

    /**
     * type:spu / product
     * 留存率随购买次数的变化图
     * @return
     */
    @GetMapping("/retentionInPurchaseTimes")
    public ResponseBo retentionInPurchaseTimes(@RequestParam("type") String type,@RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.retentionInPurchaseTimes(type, id, period));
    }

    /**
     * 件单价随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/unitPriceInPurchaseTimes")
    public ResponseBo unitPriceInPurchaseTimes(@RequestParam("type") String type,@RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.unitPriceInPurchaseTimes(type, id, period));
    }

    /**
     * 连带率随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/joinRateInPurchaseTimes")
    public ResponseBo joinRateInPurchaseTimes(@RequestParam("type") String type,@RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.joinRateInPurchaseTimes(type, id, period));
    }

    /**
     * 品类种数随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/categoryInPurchaseTimes")
    public ResponseBo categoryInPurchaseTimes(@RequestParam("type") String type,@RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.categoryInPurchaseTimes(type, id, period));
    }

    /**
     * 时间间隔随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @GetMapping("/periodInPurchaseTimes")
    public ResponseBo periodInPurchaseTimes(@RequestParam("type") String type,@RequestParam("id") String id, @RequestParam("period") String period) {
        return ResponseBo.okWithData(null, insightService.periodInPurchaseTimes(type, id, period));
    }
}
