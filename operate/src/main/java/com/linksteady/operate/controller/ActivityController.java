package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.service.ActivityDetailService;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityHeadService activityHeadService;

    @Autowired
    private ActivityDetailService activityDetailService;

    @Autowired
    private ActivityWeightService activityWeightService;

    /**
     * 获取头表的分页数据
     */
    @GetMapping("/gePageOfHead")
    public ResponseBo gePageOfHead(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String name = request.getParam().get("name");

        List<ActivityHead> dataList = activityHeadService.getDataListOfPage(start, end, name);
        int count = activityHeadService.getDataCount(name);

        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 用户成长数量分布图
     * @param headId
     * @param endDt
     * @return
     */
    @GetMapping("/getUserDataCount")
    public ResponseBo getUserDataCount(String startDt, String endDt, String dateRange) {
        return ResponseBo.okWithData(null, activityDetailService.getUserCountData(startDt, endDt, dateRange));
    }

    /**
     * 活动权重指数分布图
     * @param headId
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getWeightIdx")
    public ResponseBo getWeightIdx(String startDt, String endDt, String dateRange) {
        return ResponseBo.okWithData(null, activityWeightService.getWeightIdx(startDt, endDt, dateRange));
    }

    @GetMapping("/updateData")
    public ResponseBo updateData(String headId, String startDt, String endDt, String dateRange, String actType) {
        activityHeadService.updateData(headId, startDt, endDt, dateRange, actType);
        return ResponseBo.ok();
    }

    @GetMapping("/addData")
    public ResponseBo addData(String actName, String startDt, String endDt,String dateRange, String actType) {
        activityHeadService.addData(actName, startDt, endDt, dateRange, actType);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById(String headId) {
        return ResponseBo.okWithData(null, activityHeadService.getDataById(headId));
    }

    /**
     * 获取自主类型的开始和结束日期
     * @return
     */
    @GetMapping("/getStartAndEndDate")
    public ResponseBo getStartAndEndDate() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("start", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.put("end", LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return ResponseBo.okWithData(null, result);
    }


    /**
     * 活动获取影响开始日期和结束日期
     * @return
     */
    @GetMapping("/getEffectDateInfo")
    public ResponseBo getEffectDateInfo(String beginDate,String endDate) {
        Map<String,String> result=activityWeightService.getEffectDate(beginDate,endDate);
        return ResponseBo.okWithData(null, result);
    }
}
