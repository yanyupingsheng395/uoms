package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyPersonal;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.DailyConfigService;
import com.linksteady.operate.service.DailyDetailService;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.DailyPersonalVO;
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
    private PushConfig pushConfig;

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
     * 获取用户的文案列表(预览推送第二步)
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
    public ResponseBo getUserStatsData(Long headId) {
        // 首先判断状态和任务日期 如果任务是待执行及当天的任务，则执行文案、优惠券匹配
        DailyHead dailyHead=dailyService.getDailyHeadById(headId);
        if(null==dailyHead)
        {
            return ResponseBo.error("不存在的每日运营计划!");
        }

        String currentDay=DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        if("todo".equals(dailyHead.getStatus())&&currentDay.equals(dailyHead.getTouchDtStr()))
        {
            //验证配置是否通过校验
            if( dailyConfigService.validUserGroup())
            {
                return ResponseBo.error("成长组尚未完成配置，请先进行配置!");
            }else
            {
                //首先获取锁
                if (dailyService.getTransContentLock(String.valueOf(headId))) {
                    try {
                        dailyDetailService.generatePushList(headId);

                        //查询数据并返回
                        return ResponseBo.okWithData("", dailyService.getUserStatsData(headId));
                    } catch (Exception e) {
                        log.error("每日运营[微信]转化生成文案错误，异常堆栈为{}", e);
                        return ResponseBo.error("每日运营[微信]生成文案错误，请联系系统运维人员！");
                    } finally {
                        //释放锁
                        dailyService.delTransLock();
                    }
                } else {
                    return ResponseBo.error("其他用户正在生成文案，请稍后再操作！");
                }
            }
        }else
        {
            //直接查询数据并返回
            return ResponseBo.okWithData("", dailyService.getUserStatsData(headId));
        }
    }

    /**
     * 预览 spuName上的点击事件
     *
     * @return
     */
    @GetMapping("/getProdCountBySpu")
    public ResponseBo getProdCountBySpu(Long headId,String spuName) {
        return ResponseBo.okWithData(null,dailyService.getProdCountBySpu(headId,spuName));
    }

    /**
     * 预览 userValue上的点击事件
     *
     * @return
     */
    @GetMapping("/getMatrixData")
    public ResponseBo getMatrixData(Long headId,String userValue) {
        return ResponseBo.okWithData(null,dailyService.getMatrixData(headId,userValue));
    }

    /**
     * 启动群组推送
     *
     * @return
     */
    @GetMapping("/submitData")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo submitData(Long headId, String pushMethod, String pushPeriod, Long effectDays) {
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
    private String smsContentValid(Long headId) {
        List<Map<String, Object>> smsContentList = dailyDetailService.getContentList(headId);
        int totalSize = smsContentList.size();

        //判断是否有短信内容为空
        long nullContentSize = smsContentList.stream().filter(x -> StringUtils.isEmpty(x.get("content"))).count();
        if (nullContentSize > 0) {
            return "文案内容为空，合计：" + totalSize + "条，内容为空：" + nullContentSize + "条";
        }

        // 短信长度超出限制
        List<String> lengthIds = smsContentList.stream().filter(x -> String.valueOf(x.get("content")).length() > pushConfig.getSmsLengthLimit())
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
    public ResponseBo getStatusById(@RequestParam Long headId) {
        return ResponseBo.okWithData(null, dailyService.getDailyHeadById(headId).getStatus());
    }

    /**
     * 获取当前日期和任务日期、任务天数
     *
     * @param headId
     * @return
     */
    @GetMapping("/getCurrentAndTaskDate")
    public ResponseBo getCurrentAndTaskDate(@RequestParam Long headId) {
        return ResponseBo.okWithData(null, dailyService.getDailyHeadById(headId));
    }

    /**
     * 获取推送数据
     *
     * @return
     */
    @GetMapping("/getPushData")
    public ResponseBo getPushData(@RequestParam("headId") Long headId) {
        return ResponseBo.okWithData(null, dailyService.getPushData(headId));
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
        DailyPersonalVO dailyPersonalVo = new DailyPersonalVO();
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
        int count = dailyService.getDailyPersonalEffectCount(new DailyPersonalVO(), headId);
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
                return dailyService.getDailyPersonalEffect(new DailyPersonalVO(), limit, offset, headId);
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
        result.put("method", pushConfig.getPushMethod());
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

    @GetMapping("/getTouchDt")
    public ResponseBo getTouchDt(@RequestParam("headId") Long headId) {
        return ResponseBo.okWithData(null, dailyService.getDailyHeadById(headId).getTouchDtStr());
    }

    @GetMapping("/getLifeCycleByUserId")
    public ResponseBo getLifeCycleByUserId(@RequestParam("userId") String userId, @RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyService.getLifeCycleByUserId(userId, headId));
    }
}
