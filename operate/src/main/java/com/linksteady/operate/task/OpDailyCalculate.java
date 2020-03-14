package com.linksteady.operate.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.ExecSteps;
import com.linksteady.operate.exception.LinkSteadyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 对推送效果进行计算
 * @author huang
 */
@Slf4j
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
                //每日运营
                commonExecutors.executeSteps(ExecType.EFFECT_DAILY_KEY.getKey());
                //活动运营
                commonExecutors.executeSteps(ExecType.EFFECT_ACTIVITY_KEY.getKey());
                //手工推送
                commonExecutors.executeSteps(ExecType.EFFECT_MANUAL_KEY.getKey());

            }else if(ExecType.EFFECT_DAILY_KEY.getKey().equals(param))
            {
                //每日运营
                commonExecutors.executeSteps(ExecType.EFFECT_DAILY_KEY.getKey());

            }else if(ExecType.EFFECT_ACTIVITY_KEY.getKey().equals(param))
            {
                //活动运营
                commonExecutors.executeSteps(ExecType.EFFECT_ACTIVITY_KEY.getKey());

            }else if(ExecType.EFFECT_MANUAL_KEY.getKey().equals(param))
            {
                //手工推送
                commonExecutors.executeSteps(ExecType.EFFECT_MANUAL_KEY.getKey());
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


