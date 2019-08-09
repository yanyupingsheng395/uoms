package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import com.linksteady.operate.thread.PushListThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 每日运营
 * @author hxcao
 * @date 2019-07-31
 */
@RestController
@RequestMapping("/daily")
public class DailyController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyDetailService dailyDetailService;

    @Autowired
    private DailyGroupService dailyGroupService;

    @Autowired
    private DailyEffectService dailyEffectService;


    @Autowired
    private DailyExecuteService dailyExecuteService;

    @GetMapping("/getPageList")
    public ResponseBo getPageList(QueryRequest request) {

        int start = request.getStart();
        int end = request.getEnd();
        String touchDt = request.getParam().get("touchDt");

        int count = dailyService.getTotalCount(touchDt);
        List<DailyInfo> dailyInfos = dailyService.getPageList(start, end, touchDt);

        return ResponseBo.okOverPaging(null, count, dailyInfos);
    }

    /**
     * 触达记录
     * @param request
     * @return
     */
    @GetMapping("/getTouchPageList")
    public ResponseBo getTouchPageList(QueryRequest request) {

        int start = request.getStart();
        int end = request.getEnd();
        String touchDt = request.getParam().get("touchDt");

        int count = dailyService.getTouchTotalCount(touchDt);
        List<DailyInfo> dailyInfos = dailyService.getTouchPageList(start, end, touchDt);

        return ResponseBo.okOverPaging(null, count, dailyInfos);
    }

    @GetMapping("/getTargetType")
    public ResponseBo getTargetType(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyDetailService.getTargetType(headId));
    }

    @GetMapping("/getUrgency")
    public ResponseBo getUrgency(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyDetailService.getUrgency(headId));
    }

    @GetMapping("/getGroupDataList")
    public ResponseBo getGroupDataList(QueryRequest request) {
        String headId = request.getParam().get("headId");
        int start = request.getStart();
        int end = request.getEnd();
        int count = dailyGroupService.getGroupDataCount(headId);
        List<DailyGroup> dataList = dailyGroupService.getDataList(headId, start, end);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @GetMapping("/getDetailPageList")
    public ResponseBo getDetailPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String groupIds = request.getParam().get("groupIds");

        List<DailyDetail> dataList = dailyDetailService.getPageList(start, end, headId, groupIds);
        int count = dailyDetailService.getDataCount(headId, groupIds);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @GetMapping("/getEffectPageList")
    public ResponseBo getEffectPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String touchDt = request.getParam().get("touchDt");
        List<DailyEffect> dailyEffectList = dailyEffectService.getPageList(start, end, touchDt);
        int count = dailyEffectService.getDataCount(touchDt);
        return ResponseBo.okOverPaging(null, count, dailyEffectList);
    }

    /**
     * 获取编辑页xxx，共x人，选择y人
     * @param headId
     * @return
     */
    @GetMapping("/getTipInfo")
    public ResponseBo getTipInfo(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getTipInfo(headId));
    }

    /**
     * 提交数据，更改状态ready_push
     * @return
     */
    @GetMapping("/submitData")
    public ResponseBo submitData(String headId) {
        // 生成推送名单中
        String status = "ready_push";
        dailyService.updateStatus(headId, status);
        return ResponseBo.ok();
    }

    /**
     * 更改group是否选中
     * @param headId
     * @return
     */
    @GetMapping("/updateGroupCheck")
    public ResponseBo updateGroupCheck(String headId, String groups) {
        List<DailyGroup> groupList = JSONArray.parseArray(groups).toJavaList(DailyGroup.class);
        dailyGroupService.updateIsChecked(headId, groupList);
        // 更改实际选择人数
        List<String> groupIds = groupList.stream().map(x->x.getGroupId().toString()).collect(Collectors.toList());
        dailyService.updateCheckNum(headId, groupIds);
        return ResponseBo.ok();
    }

    @GetMapping("/getOriginalGroupCheck")
    public ResponseBo getOriginalGroupCheck() {
        List<Map<String, Object>> dataList = dailyGroupService.getOriginalGroupCheck();
        return ResponseBo.okWithData(null, dataList);
    }

    /**
     * 推送名单
     * @return
     */
    @GetMapping("/pushList")
    public ResponseBo pushList(@RequestParam String headId) {
        // 推送名单
        String status = "doing";
        dailyService.updateStatus(headId, status);
        PushListThread.generatePushList(headId);
        return ResponseBo.ok();
    }

    @GetMapping("/getStatusById")
    public ResponseBo getStatusById(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getStatusById(headId));
    }

    /**
     * 获取当前时间的指标值
     * @param headId
     * @return
     */
    @GetMapping("/getKpiVal")
    public ResponseBo getKpiVal(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getKpiVal(headId));
    }

    /**
     * 获取效果统计
     * @param headId
     * @return
     */
    @GetMapping("/getKpiStatis")
    public ResponseBo getKpiStatis(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyEffectService.getKpiStatis(headId));
    }

    @GetMapping("/getKpiTrend")
    public ResponseBo getKpiTrend(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyExecuteService.getKpiTrend(headId));
    }

    /**
     * 获取当前日期和任务日期
     * @param headId
     * @return
     */
    @GetMapping("/getCurrentAndTaskDate")
    public ResponseBo getCurrentAndTaskDate(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getCurrentAndTaskDate(headId));
    }

    /**
     * 根据紧迫度和活跃度获取系统推荐的群组
     * ids:紧迫度ID + "," + 活跃度ID
     * @return
     */
    @GetMapping("/getSelectedGroup")
    public ResponseBo getSelectedGroup(@RequestParam String headId, String activeIds, String growthIds) {
        return ResponseBo.okWithData(null, dailyGroupService.getSelectedGroup(headId, activeIds, growthIds));
    }

    @GetMapping("/getDefaultActive")
    public ResponseBo getDefaultActive(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyGroupService.getDefaultActive(headId));
    }

    @GetMapping("/getDefaultGrowth")
    public ResponseBo getDefaultGrowth(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyGroupService.getDefaultGrowth(headId));
    }

    @GetMapping("/setGroupCheck")
    public ResponseBo setGroupCheck(@RequestParam("headId") String headId, @RequestParam("groupIds") String groupIds) {
        dailyGroupService.setGroupCheck(headId, groupIds);
        return ResponseBo.ok();
    }

    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam String headId, @RequestParam String groupId, @RequestParam String smsCode) {
        dailyGroupService.setSmsCode(headId, groupId, smsCode);
        return ResponseBo.ok();
    }
}
