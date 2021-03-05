package com.linksteady.qywx.task;

import com.linksteady.qywx.service.*;
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
    MappingService mappingService;

    @Autowired
    QywxTagService qywxTagService;

    @Autowired
    private QywxChatService qywxChatService;

    @Override
    public ResultInfo execute(String param) {
        try {
            //同步基础数据
            followUserService.syncDept();
            followUserService.syncQywxFollowUser();
            externalContactService.syncExternalContact();

            //进行外部联系人和w_users表的匹配
            mappingService.mappingAll();
            mappingService.unMappingAll();

            //同步企业微信的标签
            qywxTagService.syncQywxTagList();

            //同步群数据
            qywxChatService.syncQywxChatList();

            //更新群统计数据
            qywxChatService.syncChatStatistics();

            log.info("同步企业微信数据执行成功");
            return ResultInfo.success("");
        } catch (Exception e) {
            log.error("SyncQywxData失败，错误原因为{}",e);
            return ResultInfo.faild(e.getMessage());
        }
    }
}
