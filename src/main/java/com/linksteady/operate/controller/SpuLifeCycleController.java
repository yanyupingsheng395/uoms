package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.SpuLifeCycleService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hxcao on 2019-05-10
 */
@RestController
@RequestMapping("/spuLifeCycle")
public class SpuLifeCycleController {

    @Autowired
    private SpuLifeCycleService spuLifeCycleService;

    /**
     * 留存率随购买次数变化表
     * @return
     */
    @GetMapping("/retentionPurchaseTimes")
    public ResponseBo retentionPurchaseTimes(String spuId) {
        Echart echart = spuLifeCycleService.retentionPurchaseTimes(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getPurchDateChart")
    public ResponseBo getPurchDateChart(String spuId, String type){
        Echart echart = spuLifeCycleService.getPurchDateChart(spuId, type);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getUnitPriceChart")
    public ResponseBo getUnitPriceChart(String spuId) {
        Echart echart = spuLifeCycleService.getUnitPriceChart(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getDtPeriodChart")
    public ResponseBo getDtPeriodChart(String spuId) {
        Echart echart = spuLifeCycleService.getDtPeriodChart(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getRateChart")
    public ResponseBo getRateChart(String spuId) {
        Echart echart = spuLifeCycleService.getRateChart(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getCateChart")
    public ResponseBo getCateChart(String spuId) {
        Echart echart = spuLifeCycleService.getCateChart(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getUserCountChart")
    public ResponseBo getUserCountChart(String spuId) {
        Echart echart = spuLifeCycleService.getUserCountChart(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getSaleVolumeChart")
    public ResponseBo getSaleVolumeChart(String spuId) {
        Echart echart = spuLifeCycleService.getSaleVolumeChart(spuId);
        return ResponseBo.okWithData(null, echart);
    }

    /**
     * 获取阶段节点的购买次数信息
     * @param spuId
     * @return
     */
    @GetMapping("/getStageNode")
    public ResponseBo getStageNode(String spuId) {
        return ResponseBo.okWithData(null, spuLifeCycleService.getStageNode(spuId));
    }
}
