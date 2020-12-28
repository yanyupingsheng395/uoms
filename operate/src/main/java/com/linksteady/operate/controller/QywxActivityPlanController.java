package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author huang
 * @date 2020-02-24
 */
@Slf4j
@RestController
@RequestMapping("/qywxActivityPlan")
public class QywxActivityPlanController {

    @Autowired
    private QywxActivityPlanService qywxActivityPlanService;

    @Autowired
    private QywxActivityDetailService qywxActivityDetailService;

    @Autowired
    private QywxActivityPushService qywxActivityPushService;

    @Autowired
    private PushConfig pushConfig;

    /**
     * 获取任务计划
     *
     * @param headId
     * @return
     */
    @RequestMapping("/getPlanList")
    public ResponseBo getPlanList(@RequestParam Long headId) {
        List<QywxActivityPlan> planList = qywxActivityPlanService.getPlanList(headId);
        return ResponseBo.okWithData(null, planList);
    }

    /**
     * 获取给定执行计划的群组统计信息
     *
     * @param
     * @return
     */
    @GetMapping("/getUserGroupList")
    public List<ActivityGroupVO> getUserGroupList(long planId) {
        QywxActivityPlan activityPlan = qywxActivityPlanService.getPlanInfo(planId);
        if (null == activityPlan) {
            return Lists.newArrayList();
        }
        List<ActivityGroupVO> planGroupInfo = qywxActivityPlanService.getPlanGroupList(planId);

        ActivityGroupVO activityGroupVO = new ActivityGroupVO();
        activityGroupVO.setGroupId(-1L);
        activityGroupVO.setGroupName("合计");
        activityGroupVO.setGroupUserNum(planGroupInfo.stream().mapToLong(ActivityGroupVO::getGroupUserNum).sum());
        planGroupInfo.add(activityGroupVO);
        return planGroupInfo;
    }
    /**
     * 获取推送的明细数据
     *
     * @param request
     * @return
     */
    @GetMapping("/getDetailPage")
    public ResponseBo getDetailPage(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        long planId = Long.parseLong(request.getParam().get("planId"));
        int count = qywxActivityDetailService.getDataCount(planId);
        List<QywxActivityDetail> dataList = qywxActivityDetailService.getPageList(limit, offset, planId);

        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取默认推送方式和定时推送时间
     *
     * @return
     */
    @GetMapping("/getDefaultPushInfo")
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
        return ResponseBo.okWithData(null, result);
    }

    /**
     * 开始执行计划
     *
     * @return
     */
    @PostMapping("/startPush")
    public ResponseBo startPush(@RequestParam Long planId) {
        if (StringUtils.isEmpty(planId)) {
            return ResponseBo.error("非法参数，请通过系统界面进行操作！");
        }

        QywxActivityPlan activityPlan = qywxActivityPlanService.getPlanInfo(planId);

        if (null == activityPlan) {
            return ResponseBo.error("不存在的活动执行计划，请通过系统界面进行操作！");
        }

        //仅有 待执行状态能进行推送
        if (!ActivityPlanStatusEnum.WAIT_EXEC.getStatusCode().equalsIgnoreCase(activityPlan.getPlanStatus())) {
            return ResponseBo.error("计划状态已改变，请在列表界面刷新后重新操作！");
        }

        //进行一次时间的判断 (调度修改状态有一定的延迟)
        if (!DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()).equals(String.valueOf(activityPlan.getPlanDateWid()))) {
            return ResponseBo.error("已过期的计划无法再执行!");
        }

        //进行推送的操作
        try {
            qywxActivityPushService.pushActivity(activityPlan);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("活动运营推送失败，异常堆栈为{}", e);
            //更新计划为推送失败状态
            qywxActivityPushService.updateStatusToFailed(activityPlan.getPlanId());
            if (e instanceof LinkSteadyException) {
                return ResponseBo.error(e.getMessage());
            } else {
                return ResponseBo.error("推送失败,请联系系统运维人员!");
            }
        }
    }

    /**
     * 终止执行计划
     *
     * @return
     */
    @PostMapping("/sopPlan")
    public ResponseBo sopPlan(@RequestParam Long planId) {
        if (StringUtils.isEmpty(planId)) {
            return ResponseBo.error("非法参数，请通过系统界面进行操作！");
        }

        QywxActivityPlan activityPlan = qywxActivityPlanService.getPlanInfo(planId);

        if (null == activityPlan) {
            return ResponseBo.error("不存在的活动执行计划，请通过系统界面进行操作！");
        }

        //仅有 待执行状态能进行推送
        if (!ActivityPlanStatusEnum.WAIT_EXEC.getStatusCode().equalsIgnoreCase(activityPlan.getPlanStatus())) {
            return ResponseBo.error("计划状态已改变，请在列表界面刷新后重新操作！");
        }

        //更改状态
        try {
            qywxActivityPushService.updatePlanToStop(activityPlan);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error("终止计划失败，请在列表界面刷新后重新操作");
        }
    }


    /**
     * 转化推送明细表的文案
     *
     * @param planId
     * @return
     */
    @GetMapping("/transActivityDetail")
    public ResponseBo transActivityDetail(@RequestParam Long planId) {
        String msg = "";
        //校验
        if (StringUtils.isEmpty(planId)) {
            msg = "非法请求，请通过界面进行操作!";
            return ResponseBo.error(msg);
        } else {
            QywxActivityPlan activityPlan = qywxActivityPlanService.getPlanInfo(planId);

            if (null == activityPlan) {
                msg = "非法请求，请通过界面进行操作!";
                return ResponseBo.error(msg);
            }

            if (!ActivityPlanStatusEnum.WAIT_EXEC.getStatusCode().equals(activityPlan.getPlanStatus())) {
                msg = "计划状态已改变，请刷新数据后重新操作!";
                return ResponseBo.error(msg);
            }

            //对文案配置情况进行校验
            if (qywxActivityPushService.validateSmsConfig(activityPlan)) {
                msg = "活动尚未完成文案的配置，请先完成文案的配置!";
                return ResponseBo.error(msg);
            }

            if (qywxActivityPushService.getTransActivityContentLock(activityPlan.getHeadId())) {
                try {
                    qywxActivityPushService.transActivityDetail(activityPlan);
                    return ResponseBo.ok("转化文案成功!");
                } catch (Exception e) {
                    log.error("活动运营出现转换文案异常，失败堆栈为{}", e);
                    if (e instanceof OptimisticLockException) {
                        return ResponseBo.error(e.getMessage());
                    } else {
                        return ResponseBo.error("转换文案出现未知异常！");
                    }
                } finally {
                    //释放锁
                    qywxActivityPushService.delTransLock();
                }
            } else {
                return ResponseBo.error("其他用户正在操作，请稍后再试！");
            }
        }
    }

    /**
     * 获取当前计划的汇总信息
     *
     * @param planId
     * @return
     */
    @GetMapping("/getPlanEffectInfo")
    public ResponseBo getPlanEffectInfo(@RequestParam Long planId, @RequestParam String kpiType) {
        QywxActivityPlan activityPlan = qywxActivityPlanService.getPlanInfo(planId);
        //获取计划的效果
        ActivityPlanEffectVO activitPf = qywxActivityPlanService.getPlanEffectById(planId, kpiType);
        Map<String, Object> result = Maps.newHashMap();
        result.put("planDt", activityPlan.getPlanDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        result.put("userCount", String.valueOf(activityPlan.getSuccessNum()));
        //活动效果
        result.put("activitPf", activitPf);
        return ResponseBo.okWithData(null, result);
    }

    /**
     * 获取当前计划的趋势信息
     *
     * @param planId
     * @return
     */
    @GetMapping("/getPlanEffectTrend")
    public ResponseBo getPlanEffectTrend(@RequestParam Long planId) {
        return ResponseBo.okWithData(null, qywxActivityPlanService.getPlanEffectTrend(planId));
    }

    /**
     * 执行计划-个体效果
     *
     * @return
     */
    @GetMapping("/getPersonalPlanEffect")
    public ResponseBo getDailyPersonalEffect(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long planId = Long.parseLong(request.getParam().get("planId"));
        List<ActivityPersonal> personals = qywxActivityPlanService.getPersonalPlanEffect(limit, offset, planId);
        int count = qywxActivityPlanService.getDailyPersonalEffectCount(planId);
        return ResponseBo.okOverPaging(null, count, personals);
    }

    /**
     * 获取计划的状态(获取当前head_id下某个阶段 通知计划的状态)
     *
     * @return
     */
    @GetMapping("/getPlanStatus")
    public ResponseBo getPlanStatus(@RequestParam String headId) {
        return ResponseBo.okWithData(null, qywxActivityPlanService.getPlanStatus(headId));
    }
}
