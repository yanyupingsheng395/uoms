package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONArray;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import com.linksteady.operate.thread.PushListThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
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
     * 获取目标类型
     * @param headId
     * @return
     */
    @GetMapping("/getTargetType")
    public ResponseBo getTargetType(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyDetailService.getTargetType(headId));
    }

    /**
     * 根据headId获取紧迫度列表
     * @param headId
     * @return
     */
    @GetMapping("/getUrgency")
    public ResponseBo getUrgency(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyDetailService.getUrgency(headId));
    }

    /**
     * 获取群组列表
     * @param request
     * @return
     */
    @GetMapping("/getGroupDataList")
    public ResponseBo getGroupDataList(QueryRequest request) {
        String headId = request.getParam().get("headId");
        int start = request.getStart();
        int end = request.getEnd();
        int count = dailyGroupService.getGroupDataCount(headId);
        List<DailyGroup> dataList = dailyGroupService.getDataList(headId, start, end);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取群组对应的用户列表
     * @param request
     * @return
     */
    @GetMapping("/getDetailPageList")
    public ResponseBo getDetailPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String groupIds = request.getParam().get("groupIds");
        String pathActive = request.getParam().get("pathActive");
        String sortColumn = request.getSort();
        if(null != sortColumn && sortColumn.equalsIgnoreCase("groupName")) {
            sortColumn = "GROUP_NAME";
        }
        String sortOrder = request.getSortOrder();

        List<DailyDetail> dataList = dailyDetailService.getPageList(start, end, headId, groupIds, pathActive, sortColumn, sortOrder);
        int count = dailyDetailService.getDataCount(headId, groupIds, pathActive);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     *
     * @param request
     * @return
     */
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
     * 启动群组推送
     * @return
     */
    @GetMapping("/submitData")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo submitData(String headId) {
        String status = dailyService.getStatusById(headId);
        if(!status.equalsIgnoreCase("todo")) {
            return ResponseBo.error("数据已被其它用户修改，请查看！");
        }else {
            status = "ready_push";
            dailyService.updateStatus(headId, status);
            // 计算执行率
            dailyEffectService.updateExecuteRate(headId);
            // 启动线程推送
            PushListThread.generatePushList(headId);
        }
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
     * 根据紧迫度和活跃度获取系统推荐的群组
     * ids:紧迫度ID + "," + 活跃度ID
     * @return
     */
    @GetMapping("/getSelectedGroup")
    public ResponseBo getSelectedGroup(@RequestParam String headId, String activeIds, String growthIds) {
        return ResponseBo.okWithData(null, dailyGroupService.getSelectedGroup(headId, activeIds, growthIds));
    }

    /**
     * 获取活跃度指标的选中情况
     * @param headId
     * @return
     */
    @GetMapping("/getDefaultActive")
    public ResponseBo getDefaultActive(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyGroupService.getDefaultActive(headId));
    }

    /**
     * 获取成长性指标的选中情况
     * @param headId
     * @return
     */
    @GetMapping("/getDefaultGrowth")
    public ResponseBo getDefaultGrowth(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyGroupService.getDefaultGrowth(headId));
    }

    /**
     * 更改groupId的isCheck状态为1
     * @param headId
     * @param groupIds
     * @return
     */
    @GetMapping("/setGroupCheck")
    public ResponseBo setGroupCheck(@RequestParam("headId") String headId, @RequestParam("groupIds") String groupIds) {
        dailyGroupService.setGroupCheck(headId, groupIds);
        return ResponseBo.ok();
    }

    /**
     * 设置短信模板
     * @param headId
     * @param groupId
     * @param smsCode
     * @return
     */
    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam String headId, @RequestParam String groupId, @RequestParam String smsCode) {
        dailyGroupService.setSmsCode(headId, groupId, smsCode);
        return ResponseBo.ok();
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
}
