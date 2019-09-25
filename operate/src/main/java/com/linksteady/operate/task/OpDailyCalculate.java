package com.linksteady.operate.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.dao.DailyEffectMapper;
import com.linksteady.operate.dao.DailyMapper;
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
    DailyEffectMapper dailyEffectMapper;

    @Override
    public ResultInfo execute(String param) {
        log.info("开始计算每日运营的统计数据，开始的时间为:{}, 线程名称：{}", LocalDate.now(), Thread.currentThread().getName());

        //同步触达状态
        dailyDetailMapper.synchPushStatus();
        //更新头表的完成状态
        dailyMapper.updateHeaderToFinish();
        //更新头表的推送数据状态
        dailyMapper.updateHeaderSendStatis();
        //更新推送统计状态
        dailyEffectMapper.updatePushStatInfo();

        log.info("开始计算每日运营的统计数据，结束的时间为:{}, 线程名称：{}", LocalDate.now(), Thread.currentThread().getName());
        return ResultInfo.success("success");
    }
}
