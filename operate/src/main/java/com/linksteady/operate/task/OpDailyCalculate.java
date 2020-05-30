package com.linksteady.operate.task;

import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.task.common.CommonExecutors;
import com.linksteady.operate.task.common.ExecType;
import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
   CommonExecutors commonExecutors;

    @Override
    public ResultInfo execute(String param) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("开始运营效果的计算，开始的时间为:{}", dtf2.format(LocalDateTime.now()));
        log.info("接收到的参数值为:{}",param);

        try {
            //判断参数类型
            if(StringUtils.isEmpty(param))
            {
                //每日运营 活动运营 手工推送
                commonExecutors.executeSteps(ExecType.EFFECT_DAILY_KEY.getCode(),ExecType.EFFECT_ACTIVITY_KEY.getCode(),ExecType.EFFECT_MANUAL_KEY.getCode());

            }else if(ExecType.EFFECT_DAILY_KEY.getCode().equals(param))
            {
                //每日运营
                commonExecutors.executeSteps(ExecType.EFFECT_DAILY_KEY.getCode());

            }else if(ExecType.EFFECT_ACTIVITY_KEY.getCode().equals(param))
            {
                //活动运营
                commonExecutors.executeSteps(ExecType.EFFECT_ACTIVITY_KEY.getCode());

            }else if(ExecType.EFFECT_MANUAL_KEY.getCode().equals(param))
            {
                //手工推送
                commonExecutors.executeSteps(ExecType.EFFECT_MANUAL_KEY.getCode());
            }else {
                throw new LinkSteadyException("无效的任务参数");
            }

            log.info("完成运营效果的计算，完成的时间为:{}", dtf2.format(LocalDateTime.now()));
            return ResultInfo.success("执行任务成功!");
        } catch (Exception e) {
            log.error("执行任务失败，失败的原因:{}",e);
            return ResultInfo.faild(e.getMessage());
        }

    }

}


