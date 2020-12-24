package com.linksteady.qywx.thread;

import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.service.impl.ConfigServiceImpl;
import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxTaskResultService;
import com.linksteady.qywx.service.impl.QywxTaskResultServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SyncQywxMsgExecThread implements Runnable{

    @Override
    public void run() {
        QywxTaskResultService qywxTaskResultService=(QywxTaskResultServiceImpl) SpringContextUtils.getBean("qywxTaskResultServiceImpl");
        ConfigService configService=(ConfigServiceImpl)SpringContextUtils.getBean("configServiceImpl");

        while(true)
        {
            //延迟10分钟执行
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断是否开启了企业微信消息同步
            String syncFlag=configService.getValueByName(ConfigEnum.qywxSyncMsgResult.getKeyCode());

            if(StringUtils.isEmpty(syncFlag)||"N".equals(syncFlag))
            {
                log.info("同步企业微信消息执行结果已关闭");
                continue;
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
