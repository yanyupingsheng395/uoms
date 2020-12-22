package com.linksteady.qywx.thread;

import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxTaskResultService;
import com.linksteady.qywx.service.impl.QywxTaskResultServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SyncQywxMsgExecThread implements Runnable{

    @Override
    public void run() {
        QywxTaskResultService qywxTaskResultService=(QywxTaskResultServiceImpl) SpringContextUtils.getBean("qywxTaskResultServiceImpl");

        while(true)
        {
            //延迟10分钟执行
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //同步消息执行结果
            try {
                log.info("同步企业微信消息执行结果");
                qywxTaskResultService.syncPushResult();
                qywxTaskResultService.updateDailyExecStatus();
                //更新活动运营的执行结果
                qywxTaskResultService.updateActivityExecStatus();
                //更新手工推送的执行结果
                qywxTaskResultService.updateManualExecStatus();
            } catch (WxErrorException e) {
                log.info("同步消息执行结果失败");
                e.printStackTrace();
            }
        }
    }
}
