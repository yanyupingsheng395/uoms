package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.DailyConfigService;
import com.linksteady.operate.service.DailyDetailService;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.DailyPersonalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
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
public class DailyTaskController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyDetailService dailyDetailService;

    @Autowired
    private PushProperties pushProperties;

    @Autowired
    private ConfigService configService;

    @Autowired
    private DailyConfigService dailyConfigService;

    /**
     * 获取每日成长任务分页列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getPageList")
    public ResponseBo getPageList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String touchDt = request.getParam().get("touchDt");
        int count = dailyService.getTotalCount(touchDt);
        List<DailyHead> dailyInfos = dailyService.getPageList(limit, offset, touchDt);

        //设置当前天记录的 校验状态
        String validateLabel = dailyConfigService.validUserGroup() ? "未通过" : "通过";

        dailyInfos.stream().forEach(p -> {
            p.setValidateLabel(validateLabel);
        });

        return ResponseBo.okOverPaging(validateLabel, count, dailyInfos);
    }

    /**
     * 生成待推送的短信文案
     *
     * @return
     */
    @GetMapping("/generatePushList")
    public ResponseBo generatePushList(String headId) {

        //首先获取锁
        if (dailyService.getTransContentLock(headId)) {
            try {
                dailyDetailService.generatePushList(headId);
                return ResponseBo.ok();
            } catch (Exception e) {
                log.error("每日运营转化生成文案错误，异常堆栈为{}", e);
                return ResponseBo.error("生成文案错误，请联系系统运维人员！");
            } finally {
                //释放锁
                dailyService.delTransLock();
            }
        } else {
            return ResponseBo.error("其他用户正在生成文案，请稍后再操作！");
        }
    }

    /**
     * 获取用户的文案
     *
     * @return
     */
    @GetMapping("/getUserStrategyList")
    public ResponseBo getUserStrategyList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String headId = request.getParam().get("headId");
        List<DailyDetail> dataList = dailyDetailService.getStrategyPageList(limit, offset, headId);
        int count = dailyDetailService.getStrategyCount(headId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取用户预览数据
     *
     * @return
     */
    @GetMapping("/getUserStatsData")
    public ResponseBo getUserStatsData(String headId) {
        return ResponseBo.okWithData("", dailyService.getUserStatsData(headId));
    }

    /**
     * 用户预览 刷新SPU表格和PROD表格
     */
    @GetMapping("/refreshUserStatData")
    public ResponseBo refreshUserStatData(String headId, String userValue, String pathActive, String lifecycle) {
        Map<String, String> pathActiveMap = configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap = configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap = configService.selectDictByTypeCode("LIFECYCLE");

        Map<String, Object> result = Maps.newHashMap();

        List<DailyUserStats> spuList = dailyService.getUserStatsBySpu(headId, userValue, pathActive, lifecycle);
        result.put("spuList", spuList);
        result.put("groupName", userValueMap.get(userValue) + "," + pathActiveMap.get(pathActive) + "," + lifeCycleMap.get(lifecycle) + "群组");

        DailyUserStats dailyUserStats2 = spuList.get(0);

        //获取top10 推荐商品
        if (null != dailyUserStats2) {
            List<DailyUserStats> prodList = dailyService.getUserStatsByProd(headId, userValue, pathActive, lifecycle, dailyUserStats2.getSpuName());
            result.put("prodList", prodList);
            result.put("prodGroupName", userValueMap.get(userValue) + "-" + pathActiveMap.get(pathActive) + "-" + lifeCycleMap.get(lifecycle) + "群组," + dailyUserStats2.getSpuName() + "类目");
            result.put("spuName", dailyUserStats2.getSpuName());
        } else {
            result.put("prodList", Lists.newArrayList());
            result.put("prodGroupName", "");
            result.put("spuName", "");
        }
        return ResponseBo.okWithData("", result);
    }

    /**
     * 用户预览 刷新PROD表格
     */
    @GetMapping("/refreshUserStatData2")
    public ResponseBo refreshUserStatData2(String headId, String userValue, String pathActive, String lifecycle, String spuName) {
        Map<String, String> pathActiveMap = configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap = configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap = configService.selectDictByTypeCode("LIFECYCLE");
        Map<String, Object> result = Maps.newHashMap();
        List<DailyUserStats> prodList = dailyService.getUserStatsByProd(headId, userValue, pathActive, lifecycle, spuName);
        result.put("prodList", prodList);
        result.put("prodGroupName", userValueMap.get(userValue) + "," + pathActiveMap.get(pathActive) + "," + lifeCycleMap.get(lifecycle) + "群组" + spuName + "类目");
        return ResponseBo.okWithData("", result);
    }


    /**
     * 启动群组推送
     *
     * @return
     */
    @GetMapping("/submitData")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo submitData(String headId, String pushMethod, String pushPeriod, Long effectDays) {
        //对参数进行校验
        if (StringUtils.isEmpty(headId) || StringUtils.isEmpty(pushMethod)) {
            return ResponseBo.error("参数错误，请通过系统界面进行操作！");
        }

        if (null == effectDays || effectDays < 1 || effectDays > 10) {
            return ResponseBo.error("参数错误，请通过系统界面进行操作！");
        }

        if ("FIXED".equals(pushMethod) && StringUtils.isEmpty(pushPeriod)) {
            return ResponseBo.error("参数错误，请通过系统界面进行操作！");
        }

        //进行一次状态的判断
        DailyHead dailyHead = dailyService.getDailyHeadById(headId);

        //进行一次时间的判断 (调度修改状态有一定的延迟)
        if (!DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()).equals(dailyHead.getTouchDtStr())) {
            return ResponseBo.error("已过期的任务无法再执行!");
        }

        if (null == dailyHead || !dailyHead.getStatus().equalsIgnoreCase("todo")) {
            return ResponseBo.error("记录已被其他用户修改，请返回刷新后重试！");
        }

        String validateLabel = dailyConfigService.validUserGroup() ? "未通过" : "通过";
        if (validateLabel.equalsIgnoreCase("未通过")) {
            return ResponseBo.error("成长组配置验证未通过！");
        }

        // 短信文案的校验  (是否包含变量，短信长度)
        String validResult = smsContentValid(headId);
        if (null != validResult) {
            return ResponseBo.error(validResult);
        }

        try {
            dailyService.pushContent(dailyHead, pushMethod, pushPeriod, effectDays);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("每日运营推送错误，错误堆栈为{}", e);
            if (e instanceof OptimisticLockException) {
                return ResponseBo.error(e.getMessage());
            } else {
                return ResponseBo.error("推送出现未知错误，请联系系统运维人员!");
            }
        }
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

        //判断是否有短信内容为空
        long nullContentSize = smsContentList.stream().filter(x -> StringUtils.isEmpty(x.get("content"))).count();
        if (nullContentSize > 0) {
            return "文案内容为空，合计：" + totalSize + "条，内容为空：" + nullContentSize + "条";
        }

        // 短信长度超出限制
        List<String> lengthIds = smsContentList.stream().filter(x -> String.valueOf(x.get("content")).length() > pushProperties.getSmsLengthLimit())
                .map(y -> String.valueOf(y.get("id"))).collect(Collectors.toList());
        // 短信含未被替换的模板变量
        List<String> invalidIds = smsContentList.stream().filter(x -> String.valueOf(x.get("content")).contains("$"))
                .map(y -> String.valueOf(y.get("id"))).collect(Collectors.toList());
        if (0 != lengthIds.size()) {
            return "文案长度超出限制，合计：" + totalSize + "条，不符合规范：" + lengthIds.size() + "条";
        }
        if (0 != invalidIds.size()) {
            return "文案含未不符合规范的字符，合计：" + totalSize + "条，不符合规范：" + invalidIds.size() + "条";
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
        return ResponseBo.okWithData(null, dailyService.getDailyHeadById(headId).getStatus());
    }

    /**
     * 获取当前日期和任务日期、任务天数
     *
     * @param headId
     * @return
     */
    @GetMapping("/getCurrentAndTaskDate")
    public ResponseBo getCurrentAndTaskDate(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getDailyHeadById(headId));
    }

    /**
     * 获取推送数据
     *
     * @return
     */
    @GetMapping("/getPushData")
    public ResponseBo getPushData(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyService.getPushData(headId));
    }


    /**
     * 每日成长任务用户群组设置短信内容
     *
     * @param groupId
     * @param smsCode
     * @return
     */
    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam String groupId, @RequestParam String smsCode) {
        // 判断该群组是否已经设置了文案，且是否有券这个标记与待设置文案的一致。
        // 判断是否设置了文案
        Map<String, Object> validMap = dailyService.getSelectedUserGroup(groupId);
        if ((null == validMap.get("SMS_CODE")) || StringUtils.isEmpty(validMap.get("SMS_CODE").toString())) {
            dailyService.setSmsCode(groupId, smsCode);
            return ResponseBo.ok();
        } else {
            String isCoupon = validMap.get("IS_COUPON").toString();
            int count1 = dailyService.getSmsIsCoupon(smsCode, isCoupon);
            int count2 = dailyService.getValidDailyHead();
            if (count1 > 0) {// 所选的与之前一致
                dailyService.setSmsCode(groupId, smsCode);
                return ResponseBo.ok();
            } else {
                if (count2 == 0) { // 不存在待运营的每日运营
                    dailyService.setSmsCode(groupId, smsCode);
                    return ResponseBo.ok();
                } else {
                    String msg = "";
                    if ("1".equalsIgnoreCase(isCoupon)) {
                        msg = "不允许将含券文案修改为不含券文案。";
                    } else {
                        msg = "不允许将不含券文案修改为含券文案。";
                    }
                    return ResponseBo.error("今天每日运营尚未完成推送，" + msg);
                }
            }
        }
    }

    /**
     * 每日运营-个体效果
     *
     * @return
     */
    @GetMapping("/getDailyPersonalEffect")
    public ResponseBo getDailyPersonalEffect(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String headId = request.getParam().get("headId");
        String spuIsConvert = request.getParam().get("spuIsConvert");
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        DailyPersonalVo dailyPersonalVo = new DailyPersonalVo();
        dailyPersonalVo.setSpuIsConvert(spuIsConvert);
        dailyPersonalVo.setUserValue(userValue);
        dailyPersonalVo.setPathActive(pathActive);

        List<DailyPersonal> personals = dailyService.getDailyPersonalEffect(dailyPersonalVo, limit, offset, headId);
        int count = dailyService.getDailyPersonalEffectCount(dailyPersonalVo, headId);
        return ResponseBo.okOverPaging(null, count, personals);
    }

    /**
     * 导出每日运营个体结果表
     *
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
                int limit = start - 1;
                int offset = end - start + 1;
                return dailyService.getDailyPersonalEffect(new DailyPersonalVo(), limit, offset, headId);
            });
        }

        List<Future<List<DailyPersonal>>> futures = service.invokeAll(tmp);
        futures.stream().forEach(x -> {
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
        result.put("method", pushProperties.getPushMethod());
        int hour = LocalTime.now().getHour();
        final List<String> timeList = IntStream.rangeClosed(8, 22).filter(x -> x > hour).boxed().map(y -> {
            if (y < 10) {
                return "0" + y + ":00";
            }
            return y + ":00";
        }).collect(Collectors.toList());
        result.put("timeList", timeList);

        result.put("effectDays", configService.getValueByName("op.daily.default.effectDays"));
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/getDefaultGroup")
    public ResponseBo getDefaultGroup() {
        return ResponseBo.okWithData(null, configService.getValueByName("op.daily.pathactive.list"));
    }

    @GetMapping("/setDefaultGroup")
    public ResponseBo setDefaultGroup(@RequestParam("active") String active) {
        //设置默认活跃度 第一个参数为key 第二个参数为value
        configService.updateConfig("op.daily.pathactive.list", active);
        return ResponseBo.ok();
    }

    @GetMapping("/getTouchDt")
    public ResponseBo getTouchDt(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyService.getTouchDt(headId));
    }
}
