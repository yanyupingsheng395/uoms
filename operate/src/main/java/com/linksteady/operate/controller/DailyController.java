package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
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
    private DailyPushService dailyPushService;

    @GetMapping("/getPageList")
    public ResponseBo getPageList(QueryRequest request) {

        int start = request.getStart();
        int end = request.getEnd();
        String touchDt = request.getParam().get("touchDt");

        int count = dailyService.getTotalCount(touchDt);
        List<DailyInfo> dailyInfos = dailyService.getPageList(start, end, touchDt);

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
    public ResponseBo getGroupDataList(String headId) {
        return ResponseBo.okOverPaging(null, 0, dailyGroupService.getDataList(headId));
    }

    @GetMapping("/getDetailPageList")
    public ResponseBo getDetailPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String groupId = request.getParam().get("groupId");
        List<DailyDetail> dataList = dailyDetailService.getPageList(start, end, headId, groupId);
        int count = dailyDetailService.getDataCount(headId, groupId);
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
     * 提交数据，更改状态，生成名单
     * @return
     */
    @GetMapping("/submitData")
    public ResponseBo submitData(String headId) {
        // 生成推送名单中
        String status = "pre_push";
        dailyService.updateStatus(headId, status);
        return ResponseBo.ok();
    }

    /**
     * 更改group是否选中
     * @param headId
     * @param groupId
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

    @GetMapping("/getPushList")
    public ResponseBo getPushList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        List<DailyPush> dataList = dailyPushService.getPageList(start, end, headId);
        int count = dailyPushService.getDataTotalCount(headId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }
}
