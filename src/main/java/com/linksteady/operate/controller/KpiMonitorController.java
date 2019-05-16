package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.service.KpiMonitorService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 运营指标监控相关的controller
 * @author  linkSteady
 */
@RestController
@RequestMapping("/kpiMonitor")
public class KpiMonitorController extends BaseController {

    @Autowired
    KpiMonitorService kpiMonitorService;

    /**
     * 获取留存率同期群数据
     * @return
     */
    @RequestMapping("/getRetainData")
    public ResponseBo getRetainData(@RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getRetainData(periodType, start));
    }

    /**
     * 获取留存率同期群数据（按SPU查看）
     * @return
     */
    @RequestMapping("/getRetentionBySpu")
    public ResponseBo getRetentionBySpu(@RequestParam String spuId, @RequestParam String periodType,@RequestParam String startDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getRetentionBySpu(spuId, periodType, startDt));
    }

    /**
     * 获取流失率
     * @return
     */
    @RequestMapping("/getLossUserRate")
    public ResponseBo getLossUserRate(@RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getLossUserRate(periodType, start));
    }

    @RequestMapping("/getLossUserRateBySpu")
    public ResponseBo getLossUserRateBySpu(@RequestParam String spuId, @RequestParam String periodType,@RequestParam String startDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getLossUserRateBySpu(spuId, periodType, startDt));
    }

    /**
     * 获取流失用户数
     * @return
     */
    @RequestMapping("/getLossUser")
    public ResponseBo getLossUser(@RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getLossUser(periodType, start));
    }

    /**
     * 获取流失用户数
     * @return
     */
    @RequestMapping("/getLossUserBySpu")
    public ResponseBo getLossUserBySpu(@RequestParam String spuId, @RequestParam String periodType,@RequestParam String startDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getLossUserBySpu(spuId, periodType, startDt));
    }

    /**
     * 获取留存用户数
     * @return
     */
    @RequestMapping("/getRetainUserCount")
    public ResponseBo getRetainUserCount(@RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getRetainUserCount(periodType, start));
    }

    /**
     * 获取留存用户数
     * @return
     */
    @RequestMapping("/getRetainUserCountBySpu")
    public ResponseBo getRetainUserCountBySpu(@RequestParam String spuId, @RequestParam String periodType,@RequestParam String startDt) {
        return ResponseBo.okWithData(null, kpiMonitorService.getRetainUserCountBySpu(spuId, periodType, startDt));
    }

    @RequestMapping("/getUpriceData")
    public ResponseBo getUpriceData(@RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getUpriceData(periodType, start));
    }

    @RequestMapping("/getUpriceDataBySpu")
    public ResponseBo getUpriceDataBySpu(@RequestParam String spuId, @RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getUpriceDataBySpu(spuId, periodType, start));
    }

    @RequestMapping("/getPriceData")
    public ResponseBo getPriceData(@RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getPriceData(periodType, start));
    }

    @RequestMapping("/getPriceDataBySpu")
    public ResponseBo getPriceDataBySpu(@RequestParam String spuId, @RequestParam String periodType,@RequestParam String start) {
        return ResponseBo.okWithData(null, kpiMonitorService.getPriceDataBySpu(spuId, periodType, start));
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
}

