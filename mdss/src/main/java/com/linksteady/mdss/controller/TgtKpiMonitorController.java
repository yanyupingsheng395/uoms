package com.linksteady.mdss.controller;

/**
 * Created by hxcao on 2019-05-24
 */

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.mdss.service.TargetListService;
import com.linksteady.mdss.service.TgtMonitorService;
import com.linksteady.mdss.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 目标-指标异常监控controller类
 */
@RestController
@RequestMapping("/tgtKpiMonitor")
public class TgtKpiMonitorController {

    @Autowired
    private TargetListService targetListService;

    @Autowired
    private TgtMonitorService tgtMonitorService;

    @GetMapping("/getMonitorVal")
    public ResponseBo getMonitorVal(@RequestParam("id") String targetId) {
        Map<String, Object> result = targetListService.getMonitorVal(targetId);
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("getCharts")
    public ResponseBo getCharts(@RequestParam("id") String targetId, @RequestParam("periodType") String periodType,@RequestParam("dt") String dt) {
        List<Echart> result = tgtMonitorService.getCharts(targetId, periodType, dt);
        return ResponseBo.okWithData(null, result);
    }
}
