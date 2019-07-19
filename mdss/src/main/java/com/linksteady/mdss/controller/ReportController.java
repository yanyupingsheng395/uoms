package com.linksteady.mdss.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.mdss.service.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营报表
 * @author huang
 */
@RestController
@RequestMapping("/report")
public class ReportController extends BaseController {

    @Autowired
    ReportService reportService;

    /**
     * 获取店铺运营日报的数据
     * @return ResponseBo对象
     */
    @RequestMapping("/getOpDaillyReportData")
    public ResponseBo getOpDaillyReportData(@RequestParam String source,@RequestParam String startDt,@RequestParam String endDt) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String lastStartDt=LocalDate.parse(startDt,df).minusYears(1).format(df).replace("-","");
        String lastEndDt=LocalDate.parse(endDt,df).minusYears(1).format(df).replace("-","");

        LinkedHashMap<String,String> result= reportService.getOpDaillyReport(source, StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""),lastStartDt,lastEndDt);
        return ResponseBo.okWithData("",result);
    }

    /**
     * 获取品牌日报的数据
     * @return ResponseBo对象
     */
    @RequestMapping("/getBrandReportData")
    public ResponseBo getBrandReportData(@RequestParam String source,@RequestParam String startDt,@RequestParam String endDt) {

        List<Map<String,String>> result= reportService.getBrandReportData(source, StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));
        return ResponseBo.okWithData("",result);
    }

    /**
     * 获取会员日报的数据
     * @return ResponseBo对象
     */
    @RequestMapping("/getUserDailyReport")
    public ResponseBo getUserDailyReport() {

        List<String> result= Lists.newArrayList();
        return ResponseBo.okOverPaging("",0,result);
    }

    /**
     * 获取来源的列表
     * @return ResponseBo对象
     */
    @RequestMapping("/getSourceList")
    public ResponseBo getSourceList() {
        List<Map<String,String>> sourceList=reportService.getSourceList();

        Map<String,String> sourceMap=Maps.newHashMap();
        sourceList.stream().forEach(k->{
            sourceMap.put(k.get("SOURCE_ID"),k.get("SOURCE_NAME"));
        });

        return ResponseBo.okWithData("",sourceMap);
    }
}
