package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 每日运营
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@RestController
@RequestMapping("/daily")
public class DailyController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyDetailService dailyDetailService;

    @Autowired
    private DailyEffectService dailyEffectService;

    @Autowired
    private DailyExecuteService dailyExecuteService;

    @Autowired
    private DailyPushService dailyPushService;

    /**
     * 获取任务列表信息
     * @param request
     * @return
     */
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

    /**
     * 根据成长性，活跃度，组Id获取对应的用户列表
     * @param request
     * @return
     */
    @GetMapping("/getDetailPageList")
    public ResponseBo getDetailPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        List<DailyDetail> dataList = dailyDetailService.getPageList(start, end, headId, userValue, pathActive);
        int count = dailyDetailService.getDataCount(headId, userValue, pathActive);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取用户成长策略表（第二步）
     * @return
     */
    @GetMapping("/getUserStrategyList")
    public ResponseBo getUserStrategyList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        List<DailyDetail> dataList = dailyDetailService.getStrategyPageList(start, end, headId);
        int count = dailyDetailService.getStrategyCount(headId);
        return ResponseBo.okOverPaging(null, count, dataList);
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
     * 启动群组推送
     * @return
     */
    @GetMapping("/submitData")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo submitData(String headId) {
        String status = dailyService.getStatusById(headId);
        if(!status.equalsIgnoreCase("todo")) {
            return ResponseBo.error("当前数据状态不支持该操作！");
        }else {
            // 更改状态
            status = "done";
            dailyService.updateStatus(headId, status);
        }
        return ResponseBo.ok();
    }

    /**
     * 根据headId获取当前记录的状态
     * @param headId
     * @return
     */
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

    /**
     * 效果评估页获取指标趋势
     * @param headId
     * @return
     */
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
     * 生成待推送的名单
     * @return
     */
    @GetMapping("/generatePushList")
    public ResponseBo generatePushList(String headId) {
        try {
            dailyPushService.generatePushList(headId);
            return ResponseBo.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("策略生成错误，请检查配置！");
        }

    }

    /**
     * 获取检查评估个体效果数据
     * @return
     */
    @GetMapping("/getUserEffect")
    public ResponseBo getUserEffect(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        String status = request.getParam().get("status");

        List<DailyDetail> dataList = dailyDetailService.getUserEffect(headId, start, end, userValue, pathActive, status);
        int count = dailyDetailService.getDataListCount(headId, userValue, pathActive, status);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取用户组配置分页数据
     * @param request
     * @return
     */
    @GetMapping("/userGroupListPage")
    public ResponseBo userGroupListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        List<DailyGroupTemplate> dataList = dailyService.getUserGroupListPage(start, end);
        int count = dailyService.getUserGroupCount();
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam String groupId, @RequestParam String smsCode) {
        dailyService.setSmsCode(groupId, smsCode);
        return ResponseBo.ok();
    }
}
