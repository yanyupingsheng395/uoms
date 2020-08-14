package com.linksteady.operate.task;

import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 新进订单触发拉新
 */
@Slf4j(topic = "jobLog")
@Component
@JobHandler(value = "TriggerAddUserJob")
public class TriggerAddUserJob extends IJobHandler {
    @Override
    public ResultInfo execute(String param) {

        return null;
    }
}
