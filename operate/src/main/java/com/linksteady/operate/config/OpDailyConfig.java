package com.linksteady.operate.config;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.thread.GenPushListThread;
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
        //开启生成推送名单的线程
        log.info("-----------开启日推送生成名单线程的执行---------------------");
        GenPushListThread.getInstance().start();

        //开启推送短信的线程
        log.info("-----------开启日推送短信的线程的执行---------------------");
        PushMessageThread.getInstance().start();
    }
}
