package com.linksteady.operate.task;

import com.linksteady.operate.service.*;
import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 微任务
 * 每日运营任务/活动运营 扫描状态，使其变成 失效状态
 *
 */
@Slf4j(topic = "jobLog")
@Component
@JobHandler(value = "TaskExpire")
public class TaskExpire extends IJobHandler {

    @Autowired
    DailyService dailyService;

    @Autowired
    QywxDailyService qywxDailyService;

    @Autowired
    ActivityPlanService activityPlanService;

    @Autowired
    QywxActivityPlanService qywxActivityPlanService;

    @Autowired
    ActivityHeadService activityHeadService;

    @Autowired
    QywxActivityHeadService qywxActivityHeadService;

    @Override
    public ResultInfo execute(String param) {

        log.info("开始每日运营任务失效处理");
        //每日运营任务失效
        dailyService.expireDailyHead();
        //企微
        qywxDailyService.expireActivityDailyHead();

        //活动运营 执行计划失效
        log.info("开始 活动运营-执行计划 失效处理");
        activityPlanService.expireActivityPlan();
        //企微
        qywxActivityPlanService.expireActivityPlan();

        //活动头表失效
        log.info("开始活动运营失效处理");
        activityHeadService.expireActivityHead();
        qywxActivityHeadService.expireActivityHead();


        return ResultInfo.success("");
    }
}
