package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.domain.DailyEffect;
import com.linksteady.operate.domain.DailyInfo;
import com.linksteady.operate.service.DailyDetailService;
import com.linksteady.operate.service.DailyEffectService;
import com.linksteady.operate.service.DailyGroupService;
import com.linksteady.operate.service.DailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/getTipInfo")
    public ResponseBo getTipInfo(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getTipInfo(headId));
    }

    /**
     * 提交数据，更改状态，生成名单
     * @return
     */
    @GetMapping("/submitData")
    public ResponseBo submitData(String headId, String groupIds) {
        // 生成推送名单中
        String status = "pre_push";
        dailyService.updateStatus(headId, status);
        return null;
    }
}
