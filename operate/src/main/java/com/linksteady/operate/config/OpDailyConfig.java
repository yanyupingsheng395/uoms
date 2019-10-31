package com.linksteady.operate.config;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.thread.BatchPushMessageThread;
import com.linksteady.operate.thread.PurgeThread;
import com.linksteady.operate.thread.PushMessageThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpDailyConfig implements CommandLineRunner {

    @Autowired
    private DailyPropertiesService dailyPropertiesService;

    @Override
    public void run(String... args) throws Exception {
         startThread();
    }

    /**
     * 读取日运营的配置信息
     *
     */
    @Bean(name="dailyProperties")
    public DailyProperties dailyProperties()
    {
        DailyProperties dailyProperties=dailyPropertiesService.getDailyProperties();

        if(null==dailyProperties)
        {
            dailyProperties=new DailyProperties();
        }

        return dailyProperties;
    }

    /**
     * 开启线程
     */
    private void startThread()
    {
        //开启推送短信的线程
        log.info(">>>开启日推送短信的线程的执行");
        PushMessageThread.getInstance().start();

        log.info(">>>开启批量推送短信的线程的执行");
        BatchPushMessageThread batchPushMessageThread = new BatchPushMessageThread();
        batchPushMessageThread.setName("batch_sms_send_thread");
        batchPushMessageThread.setDaemon(true);
        batchPushMessageThread.start();

        log.info("开启防骚扰推送的对象的失效监测");
        PurgeThread.getInstance().start();
    }
}
