package com.linksteady.jobmanager.task;

import com.linksteady.common.service.TaskApiService;
import com.linksteady.jobmanager.aspect.CronTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hxcao
 * @date 2019-07-09
 */
@Slf4j
@CronTag("calGmvTask")
public class CalGmvTask {

    @Autowired
    private TaskApiService taskApiService;

    public void calGmv() {
        taskApiService.calculateGmv();
    }
}
