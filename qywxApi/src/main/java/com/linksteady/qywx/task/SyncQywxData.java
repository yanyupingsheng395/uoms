package com.linksteady.qywx.task;

import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.FollowUserService;
import com.linksteady.qywx.service.MappingService;
import com.linksteady.qywx.service.QywxTaskResultService;
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
    FollowUserService followUserService;

    @Autowired
    ExternalContactService externalContactService;

    @Autowired
    QywxTaskResultService qywxTaskResultService;

    @Autowired
    MappingService mappingService;

    @Override
    public ResultInfo execute(String param) {
        try {
            followUserService.syncDept();
            followUserService.syncQywxFollowUser();
            externalContactService.syncExternalContact();

            //todo 全量匹配
            mappingService.mappingAll();
            mappingService.unMappingAll();

            //同步发送任务的执行结果
            qywxTaskResultService.syncPushResult();
            qywxTaskResultService.updateExecStatus();

            log.info("同步企业微信任务执行成功");
            return ResultInfo.success("");
        } catch (Exception e) {
            log.error("SyncQywxData失败，错误原因为{}",e);
            return ResultInfo.faild(e.getMessage());
        }
    }
}
