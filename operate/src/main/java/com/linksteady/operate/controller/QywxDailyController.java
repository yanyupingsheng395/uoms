package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.DailyPersonalVo;
import com.linksteady.operate.vo.QywxUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 每日运营(企业微信)
 *
 * @author huang
 * @date 2020-05-12
 */
@Slf4j
@RestController
@RequestMapping("/qywxDaily")
public class QywxDailyController {

    @Autowired
    private QywxDailyService qywxDailyService;

    @Autowired
    private QywxDailyDetailService qywxDailyDetailService;

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
    @GetMapping("/getHeadList")
    public ResponseBo getHeadList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String taskDate = request.getParam().get("taskDate");
        int count = qywxDailyService.getTotalCount(taskDate);
        List<QywxDailyHeader> dailyInfos = qywxDailyService.getHeadList(limit, offset, taskDate);

        //设置当前天记录的 校验状态
        String validateLabel = dailyConfigService.validUserGroupForQywx() ? "未通过" : "通过";

        dailyInfos.stream().forEach(p -> {
            p.setValidateLabel(validateLabel);
        });

        return ResponseBo.okOverPaging(validateLabel, count, dailyInfos);
    }

    /**
     * 获取用户预览数据
     *
     * @return
     */
    @GetMapping("/getTaskOverViewData")
    public ResponseBo getTaskOverViewData(Long headId) {
        // 首先判断状态和任务日期 如果任务是待执行及当天的任务，则执行文案、优惠券匹配
        QywxDailyHeader qywxDailyHeader=qywxDailyService.getHeadInfo(headId);
        if(null==qywxDailyHeader)
        {
            return ResponseBo.error("不存在的每日运营计划！！");
        }

        String currentDay=DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
        if("todo".equals(qywxDailyHeader.getStatus())&&currentDay.equals(qywxDailyHeader.getTaskDateStr()))
        {
            //验证配置是否通过校验
            if( dailyConfigService.validUserGroupForQywx())
            {
                return ResponseBo.error("成长组尚未完成配置，请先进行配置！");
            }else
            {
                //首先获取锁
                if (qywxDailyService.getTransContentLock(String.valueOf(headId))) {
                    try {
                        qywxDailyDetailService.generate(headId);

                        //查询数据并返回
                        return ResponseBo.okWithData("", qywxDailyService.getTaskOverViewData(headId));
                    } catch (Exception e) {
                        log.error("每日运营[微信]转化生成文案错误，异常堆栈为{}", e);
                        return ResponseBo.error("每日运营[微信]生成文案错误，请联系系统运维人员！");
                    } finally {
                        //释放锁
                        qywxDailyService.delTransLock();
                    }
                } else {
                    return ResponseBo.error("其他用户正在生成文案，请稍后再操作！");
                }
            }
        }else
        {
            //直接查询数据并返回
            return ResponseBo.okWithData("", qywxDailyService.getTaskOverViewData(headId));
        }
    }


    /**
     * 获取用户明细的列表
     *
     * @return
     */
    @GetMapping("/getDetailList")
    public ResponseBo getDetailList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long headId = Long.parseLong(request.getParam().get("headId"));
        String qywxUserId="";
        List<QywxDailyDetail> dataList = qywxDailyDetailService.getQywxDetailList(headId,limit, offset,qywxUserId);
        int count = qywxDailyDetailService.getQywxDetailCount(headId,qywxUserId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @GetMapping("/getTaskDt")
    public ResponseBo getTaskDt(@RequestParam("headId") Long headId) {
        Date taskDate=qywxDailyService.getHeadInfo(headId).getTaskDate();
        return ResponseBo.okWithData(null, new SimpleDateFormat("yyyyMMdd").format(taskDate));
    }


    /**
     * 启动群组推送
     *
     * @return
     */
    @GetMapping("/submitTask")
    public ResponseBo submitTask(Long headId, String pushMethod, String pushPeriod, Long effectDays) {
        //对参数进行校验
        if (null==headId || StringUtils.isEmpty(pushMethod)) {
            return ResponseBo.error("参数错误，请通过系统界面进行操作！");
        }

        if (null == effectDays || effectDays < 1 || effectDays > 10) {
            return ResponseBo.error("参数错误，请通过系统界面进行操作！");
        }

        if ("FIXED".equals(pushMethod) && StringUtils.isEmpty(pushPeriod)) {
            return ResponseBo.error("参数错误，请通过系统界面进行操作！");
        }

        //进行一次状态的判断
        QywxDailyHeader qywxDailyHeader = qywxDailyService.getHeadInfo(headId);

        //进行一次时间的判断 (调度修改状态有一定的延迟)
        if (!DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()).equals(qywxDailyHeader.getTaskDateStr())) {
            return ResponseBo.error("已过期的任务无法再执行!");
        }

        if (null == qywxDailyHeader || !qywxDailyHeader.getStatus().equalsIgnoreCase("todo")) {
            return ResponseBo.error("当前任务非待执行状态，请返回刷新后重试！");
        }

        String validateLabel = dailyConfigService.validUserGroupForQywx() ? "未通过" : "通过";
        if (validateLabel.equalsIgnoreCase("未通过")) {
            return ResponseBo.error("成长组配置验证未通过！");
        }

        //todo 此处考虑是否再对文案进行校验

        try {
            qywxDailyService.push(qywxDailyHeader, pushMethod, pushPeriod, effectDays);
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
     * 获取当前日期和任务日期、任务天数
     *
     * @param headId
     * @return
     */
    @GetMapping("/getTaskInfo")
    public ResponseBo getTaskInfo(@RequestParam Long headId) {
        return ResponseBo.okWithData(null, qywxDailyService.getHeadInfo(headId));
    }

    /**
     * 获取推送变化数据
     *
     * @return
     */
    @GetMapping("/getPushEffectChange")
    public ResponseBo getPushEffectChange(@RequestParam("headId") Long headId) {
        return ResponseBo.okWithData(null, qywxDailyService.getPushEffectChange(headId));
    }

    /**
     * 获取已转化的明细数据
     *
     * @return
     */
    @GetMapping("/getConversionList")
    public ResponseBo getConversionList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long headId = Long.parseLong(request.getParam().get("headId"));
        String qywxUserId="";
        List<QywxDailyDetail> dataList = qywxDailyDetailService.getConversionList(headId,limit, offset,qywxUserId);
        int count = qywxDailyDetailService.getConversionCount(headId,qywxUserId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取企业微信成员列表
     *
     * @return
     */
    @GetMapping("/getQywxUserList")
    public ResponseBo getQywxUserList(QueryRequest request) {
        Long headId = Long.parseLong(request.getParam().get("headId"));

        List<QywxUserVO> dataList = qywxDailyDetailService.getQywxUserList(headId);
        return ResponseBo.okWithData(null, dataList);
    }


//    /**
//     * 每日运营-个体效果
//     *
//     * @return
//     */
//    @GetMapping("/getDailyPersonalEffect")
//    public ResponseBo getDailyPersonalEffect(QueryRequest request) {
//        int limit = request.getLimit();
//        int offset = request.getOffset();
//        String headId = request.getParam().get("headId");
//        String spuIsConvert = request.getParam().get("spuIsConvert");
//        String userValue = request.getParam().get("userValue");
//        String pathActive = request.getParam().get("pathActive");
//        DailyPersonalVo dailyPersonalVo = new DailyPersonalVo();
//        dailyPersonalVo.setSpuIsConvert(spuIsConvert);
//        dailyPersonalVo.setUserValue(userValue);
//        dailyPersonalVo.setPathActive(pathActive);
//
//        List<DailyPersonal> personals = dailyService.getDailyPersonalEffect(dailyPersonalVo, limit, offset, headId);
//        int count = dailyService.getDailyPersonalEffectCount(dailyPersonalVo, headId);
//        return ResponseBo.okOverPaging(null, count, personals);
//    }
//
//    /**
//     * 导出每日运营个体结果表
//     *
//     * @param headId
//     * @return
//     * @throws InterruptedException
//     */
//    @PostMapping("/downloadExcel")
//    public ResponseBo excel(@RequestParam("headId") String headId) throws InterruptedException {
//        List<DailyPersonal> list = Lists.newLinkedList();
//        List<Callable<List<DailyPersonal>>> tmp = Lists.newLinkedList();
//        int count = dailyService.getDailyPersonalEffectCount(new DailyPersonalVo(), headId);
//        ExecutorService service = Executors.newFixedThreadPool(10);
//        int pageSize = 1000;
//        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
//        for (int i = 0; i < pageNum; i++) {
//            int idx = i;
//            tmp.add(() -> {
//                int start = idx * pageSize + 1;
//                int end = (idx + 1) * pageSize;
//                end = end > count ? count : end;
//                int limit = start - 1;
//                int offset = end - start + 1;
//                return dailyService.getDailyPersonalEffect(new DailyPersonalVo(), limit, offset, headId);
//            });
//        }
//
//        List<Future<List<DailyPersonal>>> futures = service.invokeAll(tmp);
//        futures.stream().forEach(x -> {
//            try {
//                list.addAll(x.get());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        });
//        try {
//            return FileUtils.createExcelByPOIKit("每日运营个体结果表", list, DailyPersonal.class);
//        } catch (Exception e) {
//            log.error("导出每日运营个体结果表失败", e);
//            return ResponseBo.error("导出每日运营个体结果表失败，请联系网站管理员！");
//        }
//    }
//

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

    /**
     * 触达用户之前进行用户群组的验证 并返回验证结果 true表示验证未通过 false表示验证通过
     */
    @GetMapping("/validUserGroupForQywx")
    public ResponseBo validUserGroupForQywx() {
        return ResponseBo.okWithData(null, dailyConfigService.validUserGroupForQywx());
    }



}
