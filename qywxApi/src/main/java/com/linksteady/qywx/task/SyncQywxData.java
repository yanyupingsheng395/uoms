package com.linksteady.qywx.task;

import com.linksteady.qywx.service.SyncTaskService;
import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "syncQywxData")
public class SyncQywxData extends IJobHandler {

    @Autowired
    SyncTaskService syncTaskService;

    @Override
    public ResultInfo execute(String param) {
        try {
            syncTaskService.syncQywxData();
            return ResultInfo.success("");
        } catch (Exception e) {
            log.error("SyncQywxData失败，错误原因为{}",e);
            return ResultInfo.faild(e.getMessage());
        }
    }
}
