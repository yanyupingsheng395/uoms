package com.linksteady.operate.task;

import com.linksteady.operate.dao.DailyCalculateMapper;
import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 对推送效果进行计算
 * @author huang
 */
@Slf4j(topic = "jobLog")
@Component
@JobHandler(value = "opDailyCalculate")
public class OpDailyCalculate extends IJobHandler {

    @Autowired
    private DailyCalculateMapper dailyCalculateMapper;

    @Override
    public ResultInfo execute(String param) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("开始每日计算任务，开始的时间为:{}", dtf2.format(LocalDateTime.now()));

        try {
            log.info("开始更新推送表的状态");
            dailyCalculateMapper.execUpdatePushList();
            dailyCalculateMapper.execUpdatePushListLarge();

            log.info("根据推送表更新detail表");
            dailyCalculateMapper.execUpdateDailyDetail();
            dailyCalculateMapper.execUpdateActivityDetail();
            dailyCalculateMapper.execUpdateManualDetail();

            log.info("更新plan表");
            dailyCalculateMapper.updateActivityPlan();
            dailyCalculateMapper.updateQywxActivityPlan();


            log.info("更新头表的状态");
            dailyCalculateMapper.updateDailyHeader();
            dailyCalculateMapper.updateActivityPreheat();
            dailyCalculateMapper.updateActivityPreheatNotify();
            dailyCalculateMapper.updateActivityFormal();
            dailyCalculateMapper.updateActivityFormalNotify();

            dailyCalculateMapper.updateManualHeader();
            dailyCalculateMapper.updateManualActualPushDate();

            log.info("更新头表的统计数据");
            dailyCalculateMapper.updateDailyPushStatistics();
            dailyCalculateMapper.updateActivityPushStatistics();
            dailyCalculateMapper.updateManualPushStatistics();

            //企微更新头表的推送状态
            dailyCalculateMapper.updateQywxDailyHeader();
            dailyCalculateMapper.updateQywxActivityFormal();
            dailyCalculateMapper.updateQywxActivityFormalNotify();

            //企微头表的统计数据
            dailyCalculateMapper.updateQywxDailyPushStatistics();
            //dailyCalculateMapper.updateQywxActivityPushStatistics();
            dailyCalculateMapper.updateQywxManualPushStatistics();

            log.info("完成运营效果的计算，完成的时间为:{}", dtf2.format(LocalDateTime.now()));
            return ResultInfo.success("执行任务成功!");
        } catch (Exception e) {
            log.error("执行任务失败，失败的原因:{}",e);
            return ResultInfo.faild(e.getMessage());
        }

    }

}


