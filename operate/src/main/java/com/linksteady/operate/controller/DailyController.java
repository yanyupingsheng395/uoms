package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 每日运营
 *
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

    @Autowired
    private DailyProperties dailyProperties;

    /**
     * 获取任务列表信息
     *
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
     *
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
     *
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
     *
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
     *
     * @param headId
     * @return
     */
    @GetMapping("/getTipInfo")
    public ResponseBo getTipInfo(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getTipInfo(headId));
    }

    /**
     * 启动群组推送
     *
     * @return
     */
    @GetMapping("/submitData")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo submitData(String headId, String pushMethod, String pushPeriod) {
        String status = dailyService.getStatusById(headId);
        if (!status.equalsIgnoreCase("todo")) {
            return ResponseBo.error("当前数据状态不支持该操作！");
        }

        // 短信文案的校验  (是否包含变量，短信长度)
        String validResult = smsContentValid(headId);
        if (null != validResult) {
            return ResponseBo.error(validResult);
        }
        // 推送方式 IMME立即推送 AI智能推送
        updateSmsPushMethod(headId, pushMethod, pushPeriod);
        status = "done";
        dailyService.updateStatus(headId, status);
        return ResponseBo.ok();

    }

    /**
     * 根据推送方式更新短信推送时间
     */
    private void updateSmsPushMethod(String headId, String method, String period) {
        String pushOrderPeriod = "";
        if ("IMME".equalsIgnoreCase(method)) {
            pushOrderPeriod = String.valueOf(LocalTime.now().plusMinutes(10).getHour());
        }

        if ("FIXED".equalsIgnoreCase(method)) {
            pushOrderPeriod = String.valueOf(LocalTime.parse(period, DateTimeFormatter.ofPattern("HH:mm")).getHour());
        }
        // 默认是AI
        dailyDetailService.updatePushOrderPeriod(headId, pushOrderPeriod);
    }

    /**
     * 发送之前校验短信内容
     *
     * @param headId
     * @return
     */
    private String smsContentValid(String headId) {
        List<Map<String, Object>> smsContentList = dailyDetailService.getContentList(headId);
        // 短信长度超出限制
        List<String> lengthIds = smsContentList.stream().filter(x -> String.valueOf(x.get("CONTENT")).length() > dailyProperties.getSmsLengthLimit())
                .map(y -> String.valueOf(y.get("ID"))).collect(Collectors.toList());
        // 短信含未被替换的模板变量
        List<String> invalidIds = smsContentList.stream().filter(x -> String.valueOf(x.get("CONTENT")).contains("$"))
                .map(y -> String.valueOf(y.get("ID"))).collect(Collectors.toList());
        if (0 != lengthIds.size()) {
            String msg = String.join(",", lengthIds);
            return "短信长度超出限制，对应ID为[" + (msg.length() > 30 ? msg.substring(0, 30) + "..." : msg) + "]";
        }
        if (0 != invalidIds.size()) {
            String msg = String.join(",", invalidIds);
            return "短信含未被替换的模板变量，对应ID为[" + (msg.length() > 30 ? msg.substring(0, 30) + "..." : msg) + "]";
        }
        return null;
    }

    /**
     * 根据headId获取当前记录的状态
     *
     * @param headId
     * @return
     */
    @GetMapping("/getStatusById")
    public ResponseBo getStatusById(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getStatusById(headId));
    }

    /**
     * 获取当前时间的指标值
     *
     * @param headId
     * @return
     */
    @GetMapping("/getKpiVal")
    public ResponseBo getKpiVal(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getKpiVal(headId));
    }

    /**
     * 获取效果统计
     *
     * @param headId
     * @return
     */
    @GetMapping("/getKpiStatis")
    public ResponseBo getKpiStatis(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyEffectService.getKpiStatis(headId));
    }

    /**
     * 效果评估页获取指标趋势
     *
     * @param headId
     * @return
     */
    @GetMapping("/getKpiTrend")
    public ResponseBo getKpiTrend(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyExecuteService.getKpiTrend(headId));
    }

    /**
     * 获取当前日期和任务日期
     *
     * @param headId
     * @return
     */
    @GetMapping("/getCurrentAndTaskDate")
    public ResponseBo getCurrentAndTaskDate(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getCurrentAndTaskDate(headId));
    }

    /**
     * 生成待推送的名单
     *
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
     *
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
     *
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

    /**
     * 获取默认推送方式和定时推送时间
     *
     * @return
     */
    @GetMapping("/getPushInfo")
    public ResponseBo getPushInfo() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("method", dailyProperties.getPushMethod());
        int hour = LocalTime.now().getHour();
        final List<String> timeList = IntStream.rangeClosed(8, 22).filter(x -> x > hour).boxed().map(y -> {
            if (y < 10) {
                return "0" + y + ":00";
            }
            return y + ":00";
        }).collect(Collectors.toList());
        result.put("timeList", timeList);
        return ResponseBo.okWithData(null, result);
    }
}
