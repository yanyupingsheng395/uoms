package com.linksteady.operate.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.operate.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 对每日运营的状态进行同步，触达数据进行汇总
 * @author huang
 */
@Slf4j
@Component
@JobHandler(value = "opDailyCalculate")
public class OpDailyCalculate extends IJobHandler {

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Autowired
    private ManualHeaderMapper manualHeaderMapper;

    @Autowired
    private ManualDetailMapper manualDetailMapper;


    @Override
    public ResultInfo execute(String param) {
        log.info("开始每日运营的计算，开始的时间为:{}", LocalDate.now());

        //同步触达状态
        dailyDetailMapper.synchPushStatus();
        //更新头表的完成状态
        dailyMapper.updateHeaderToFinish();
        //更新头表的推送数据状态
        dailyMapper.updateHeaderSendStatis();

        log.info("结束每日运营的计算，结束的时间为:{}", LocalDate.now());


        log.info("开始活动运营的计算，开始的时间为:{}", LocalDate.now());

        //同步触达状态
        activityDetailMapper.synchPushStatus();
        //更新plan表的完成状态
        activityDetailMapper.updatePlanToFinish();
        //更新头表的推送数据状态(预售)
        activityDetailMapper.updatePreheatHeaderToDone();
        //更新头表的推送数据状态(正式)
        activityDetailMapper.updatePreheatHeaderToDone();

        log.info("结束活动运营的计算，结束的时间为:{}", LocalDate.now());

        log.info("开始手工推送的计算，开始的时间为:{}", LocalDate.now());
        // 手动推送短信
        // 更新行表的状态和时间
        manualDetailMapper.updateSendStatusAnDate();
        // 更新人数
        manualHeaderMapper.updateSendNum();
        // 更新状态
        manualHeaderMapper.updateSendStatus();
        // 更新推送日期
        manualHeaderMapper.updateSendPushDate();
        log.info("结束手工推送的计算，结束的时间为:{}", LocalDate.now());
        return ResultInfo.success("success");
    }
}


