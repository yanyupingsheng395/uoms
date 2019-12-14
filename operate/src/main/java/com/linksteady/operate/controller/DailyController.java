package com.linksteady.operate.controller;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.DailyPersonalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
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
    private DailyProperties dailyProperties;

    /**
     * 获取每日成长任务分页列表
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
        List<DailyHead> dailyInfos = dailyService.getPageList(start, end, touchDt);
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
     * 生成待推送的短信文案
     *
     * @return
     */
    @GetMapping("/generatePushList")
    public ResponseBo generatePushList(String headId) {
        String result;
        try {
            dailyDetailService.deletePushContentTemp(headId);
            //1表示生成成功 0表示生成失败
            result = dailyDetailService.generatePushList(headId);

            if ("1".equals(result)) {
                return ResponseBo.ok();
            } else {
                return ResponseBo.error("生成文案错误，请联系系统运维人员！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("策略生成错误，请检查配置！");
        }
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
        // 推送方式 IMME立即推送 AI智能推送 FIXED固定时间推送
        updateSmsPushMethod(headId, pushMethod, pushPeriod);

        //复制写入待推送列表
        dailyDetailService.copyToPushList(headId);

        status = "done";
        dailyService.updateStatus(headId, status);
        return ResponseBo.ok();

    }

    /**
     * 根据推送方式更新短信推送时间
     */
    private void updateSmsPushMethod(String headId, String method, String period) {
        String pushOrderPeriod = "";
        // 立即推送：当前时间往后顺延10分钟
        if ("IMME".equalsIgnoreCase(method)) {
            pushOrderPeriod = String.valueOf(LocalTime.now().plusMinutes(10).getHour());
        }

        // 固定时间推送：参数获取
        if ("FIXED".equalsIgnoreCase(method)) {
            pushOrderPeriod = String.valueOf(LocalTime.parse(period, DateTimeFormatter.ofPattern("HH:mm")).getHour());
        }
        // 默认是AI：plan_push_period = order_period 此时，pushOrderPeriod = ""
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
        int totalSize = smsContentList.size();
        // 短信长度超出限制
        List<String> lengthIds = smsContentList.stream().filter(x -> String.valueOf(x.get("CONTENT")).length() > dailyProperties.getSmsLengthLimit())
                .map(y -> String.valueOf(y.get("ID"))).collect(Collectors.toList());
        // 短信含未被替换的模板变量
        List<String> invalidIds = smsContentList.stream().filter(x -> String.valueOf(x.get("CONTENT")).contains("$"))
                .map(y -> String.valueOf(y.get("ID"))).collect(Collectors.toList());
        if (0 != lengthIds.size()) {
            return "短信长度超出限制，共：" + totalSize + "条，不符合规范：" + lengthIds.size() + "条";
        }
        if (0 != invalidIds.size()) {
            return "短信含未被替换的模板变量，共：" + totalSize + "条，不符合规范：" + invalidIds.size() + "条";
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
     * 获取推送数据
     * @return
     */
    @GetMapping("/getPushData")
    public ResponseBo getPushData(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyService.getPushData(headId));
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

    /**
     * 每日成长任务用户群组设置短信内容
     * @param groupId
     * @param smsCode
     * @return
     */
    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam String groupId, @RequestParam String smsCode) {
        dailyService.setSmsCode(groupId, smsCode);
        return ResponseBo.ok();
    }

    /**
     * 每日运营-个体效果
     * @return
     */
    @GetMapping("/getDailyPersonalEffect")
    public ResponseBo getDailyPersonalEffect(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String spuIsConvert = request.getParam().get("spuIsConvert");
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        DailyPersonalVo dailyPersonalVo = new DailyPersonalVo();
        dailyPersonalVo.setSpuIsConvert(spuIsConvert);
        dailyPersonalVo.setUserValue(userValue);
        dailyPersonalVo.setPathActive(pathActive);

        List<DailyPersonal> personals = dailyService.getDailyPersonalEffect(dailyPersonalVo, start, end, headId);
        int count = dailyService.getDailyPersonalEffectCount(dailyPersonalVo, headId);
        return ResponseBo.okOverPaging(null, count, personals);
    }

    /**
     * 导出每日运营个体结果表
     * @param headId
     * @return
     * @throws InterruptedException
     */
    @PostMapping("/downloadExcel")
    public ResponseBo excel(@RequestParam("headId") String headId) throws InterruptedException {
        List<DailyPersonal> list = Lists.newLinkedList();
        List<Callable<List<DailyPersonal>>> tmp = Lists.newLinkedList();
        int count = dailyService.getDailyPersonalEffectCount(new DailyPersonalVo(), headId);
        ExecutorService service = Executors.newFixedThreadPool(10);
        int pageSize = 1000;
        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            int idx = i;
            tmp.add(() -> {
                int start = idx * pageSize + 1;
                int end = (idx + 1) * pageSize;
                end = end > count ? count : end;
                return dailyService.getDailyPersonalEffect(new DailyPersonalVo(), start, end, headId);
            });
        }

        List<Future<List<DailyPersonal>>> futures = service.invokeAll(tmp);
        futures.stream().forEach(x-> {
            try {
                list.addAll(x.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        try {
            return FileUtils.createExcelByPOIKit("每日运营个体结果表", list, DailyPersonal.class);
        } catch (Exception e) {
            log.error("导出每日运营个体结果表失败", e);
            return ResponseBo.error("导出每日运营个体结果表失败，请联系网站管理员！");
        }
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

    /**
     * 触达用户之前进行用户群组的验证
     *
     * @return
     */
    @GetMapping("/validUserGroup")
    public ResponseBo validUserGroup() {
        return ResponseBo.okWithData(null, dailyService.validUserGroup());
    }
}
