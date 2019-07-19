package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.KpiSumeryInfo;
import com.linksteady.operate.service.UserOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hxcao on 2019-06-03
 */
@RestController
@RequestMapping("/useroperator")
public class UserOperatorController {

    @Autowired
    private UserOperatorService userOperatorService;

    /**
     * 获取所有渠道数据
     * @return
     */
    @RequestMapping("/getSource")
    public ResponseBo getSource() {
        return ResponseBo.okWithData(null, userOperatorService.getSource());
    }

    @RequestMapping("/getBrand")
    public ResponseBo getBrand() {
        return ResponseBo.okWithData(null, userOperatorService.getBrand());
    }

    @RequestMapping("/getKpiInfo")
    public ResponseBo getKpiInfo(String kpiType, String periodType, String startDt, String endDt,String source) {
        return ResponseBo.okWithData(null, userOperatorService.getKpiInfo(kpiType, periodType, startDt, endDt,source));
    }

    @RequestMapping("/getKpiChart")
    public ResponseBo getKpiChart(String kpiType, String periodType, String startDt, String endDt,String source) {
        return  ResponseBo.okWithData(null, userOperatorService.getKpiChart(kpiType, periodType, startDt, endDt,source));
    }

    /**
     * 获取首购，非首购，总体趋势图
     * @param periodType
     * @param startDt
     * @param endDt
     * @return
     */
    @RequestMapping("/getSpAndFpKpi")
    public ResponseBo getSpAndFpKpi(String kpiType, String periodType, String startDt, String endDt,String source) {
        return  ResponseBo.okWithData(null, userOperatorService.getSpAndFpKpi(kpiType, periodType, startDt, endDt,source));
    }

    /**
     * 获取首购趋势图
     */
    @RequestMapping("/getSpOrFpKpiVal")
    public ResponseBo getSpOrFpKpiVal(String kpiType, String periodType, String startDt, String endDt, String source) {
        return ResponseBo.okWithData(null, userOperatorService.getSpAndFpKpiPeriodData(kpiType, periodType, startDt, endDt, source));
    }

    /**
     * 获取首购，非首购用户的绝对值，同比，环比，贡献率或均值比
     */
    @RequestMapping("/getKpiCalInfo")
    public ResponseBo getKpiCalInfo(String source, String kpiType, String periodType, String startDt, String endDt) {
        return ResponseBo.okWithData(null, userOperatorService.getKpiCalInfo(source, kpiType, periodType, startDt, endDt));
    }

    /**
     *获取结构图的数据
     */
    @RequestMapping("/getOrgChartData")
    public ResponseBo getOrgChartData(String periodType, String startDt, String endDt,String source) {

        //获取所有的指标值
        KpiSumeryInfo result=userOperatorService.getSummaryKpiInfo(periodType,startDt,endDt,source);
        return ResponseBo.okWithData(null, result);
    }
}
