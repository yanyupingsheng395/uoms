package com.linksteady.operate.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.dao.DailyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 对活动运营的状态进行同步，触达数据进行汇总
 * @author huang
 */
@Slf4j
@Component
@JobHandler(value = "opActivityCalculate")
public class OpActivityCalculate extends IJobHandler {

    @Autowired
    private ActivityDetailMapper activityDetailMapper;


    @Override
    public ResultInfo execute(String param) {
        log.info("开始计算活动运营的统计数据，开始的时间为:{}, 线程名称：{}", LocalDate.now(), Thread.currentThread().getName());

        //同步触达状态
        activityDetailMapper.synchPushStatus();
        //更新plan表的完成状态
        activityDetailMapper.updatePlanToFinish();
        //更新头表的推送数据状态(预售)
        activityDetailMapper.updatePreheatHeaderToDone();
        //更新头表的推送数据状态(正式)
        activityDetailMapper.updatePreheatHeaderToDone();

        log.info("活动运营的统计数据，结束的时间为:{}, 线程名称：{}", LocalDate.now(), Thread.currentThread().getName());
        return ResultInfo.success("success");
    }
}


