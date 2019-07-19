package com.linksteady.operate.controller;

import com.google.common.collect.Table;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.service.SynGroupService;
import com.linksteady.operate.vo.Echart;
import com.linksteady.operate.vo.ParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运营指标监控相关的controller
 * @author  linkSteady
 */
@RestController
@RequestMapping("/kpiMonitor")
public class KpiMonitorController extends BaseController {

    @Autowired
    KpiMonitorService kpiMonitorService;

    @Autowired
    private SynGroupService synGroupService;

    /**
     * 同期群-留存率
     * @return
     */
    @RequestMapping("/getRetainData")
    public ResponseBo getRetainData(@RequestParam("periodType") String periodType, @RequestParam("start") String start, @RequestParam(required = false, value = "spuId") String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getRetentionData(paramVO, spuId));
    }

    /**
     * 获取流失率
     * @return
     */
    @RequestMapping("/getLossUserRate")
    public ResponseBo getLossUserRate(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getLossUserRate(paramVO, spuId));
    }

    /**
     * 获取流失用户数
     * @return
     */
    @RequestMapping("/getLossUser")
    public ResponseBo getLossUser(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getLossUser(paramVO, spuId));
    }

    /**
     * 获取留存用户数
     * @return
     */
    @RequestMapping("/getRetainUserCount")
    public ResponseBo getRetainUserCount(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getRetainUserCount(paramVO, spuId));
    }

    @RequestMapping("/getUpriceData")
    public ResponseBo getUpriceData(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getUpriceData(paramVO, spuId));
    }

    @RequestMapping("/getPriceData")
    public ResponseBo getPriceData(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getPriceData(paramVO, spuId));
    }

    @RequestMapping("/getUnitPriceData")
    public ResponseBo getUnitPriceData(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getUnitPriceData(paramVO, spuId));
    }

    @RequestMapping("/getJoinRateData")
    public ResponseBo getJoinRateData(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getJoinRateData(paramVO, spuId));
    }

    @RequestMapping("/getPurchFreq")
    public ResponseBo getPurchFreq(@RequestParam String periodType,@RequestParam String start, String spuId) {
        ParamVO paramVO = new ParamVO();
        paramVO.setStartDt(start);
        paramVO.setPeriodType(periodType);
        return ResponseBo.okWithData(null, synGroupService.getPurchFreq(paramVO, spuId));
    }

    @GetMapping("/getGMV")
    public ResponseBo getGMV(String startDt, String endDt, String spuId) {
        Echart echart = kpiMonitorService.getGMV(startDt, endDt, spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getTradeUser")
    public ResponseBo getTradeUser(String startDt, String endDt, String spuId) {
        Echart echart = kpiMonitorService.getTradeUser(startDt, endDt, spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/getAvgCsPrice")
    public ResponseBo getAvgCsPrice(String startDt, String endDt, String spuId) {
        Echart echart = kpiMonitorService.getAvgCsPrice(startDt, endDt, spuId);
        return ResponseBo.okWithData(null, echart);
    }

    @GetMapping("/generateFittingData")
    public ResponseBo generateFittingData(String spuId, String purchCount) {
        if(!"".equals(purchCount)) {
            List<Integer> purchTimes = Arrays.asList(purchCount.split(",")).stream().map(x->Integer.valueOf(x)).collect(Collectors.toList());
            return ResponseBo.okWithData(null, kpiMonitorService.generateFittingData(spuId, purchTimes));
        }
        return ResponseBo.okWithData(null, null);
    }

    @GetMapping("/getTotalGmv")
    public ResponseBo getTotalGmv(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getTotalGmv(startDt, endDt));
    }

    @GetMapping("/getTotalTradeUser")
    public ResponseBo getTotalTradeUser(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getTotalTradeUser(startDt, endDt));
    }

    @GetMapping("/getTotalAvgPrice")
    public ResponseBo getTotalAvgPrice(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getTotalAvgPrice(startDt, endDt));
    }

    /**
     * 平均订单价趋势图
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getOrderAvgPrice")
    public ResponseBo getOrderAvgPrice(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getOrderAvgPrice(startDt, endDt));
    }

    /**
     * 平均订单数趋势图
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getAvgOrderQuantity")
    public ResponseBo getAvgOrderQuantity(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getAvgOrderQuantity(startDt, endDt));
    }

    /**
     * 件单价趋势图
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getAvgPiecePrice")
    public ResponseBo getAvgPiecePrice(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getAvgPiecePrice(startDt, endDt));
    }

    /**
     * 平均连带率趋势图
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getAvgJoinRate")
    public ResponseBo getAvgJoinRate(String startDt, String endDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getAvgJoinRate(startDt, endDt));
    }

    @GetMapping("/getSource")
    public ResponseBo getSource() {
        Table<String, String, String> table = KpiCacheManager.getInstance().getDiagDimValueList();
        Map<String, String> result = table.row("source");
        return ResponseBo.okWithData(null, result);
    }
}

