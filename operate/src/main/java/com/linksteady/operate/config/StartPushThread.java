package com.linksteady.operate.config;

import com.linksteady.operate.thread.PushListThread;
import com.linksteady.operate.thread.PushSmsThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartPushThread implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        //开启生成推送名单的线程
       log.info("-----------开启日推送生成名单线程的执行---------------------");
       PushListThread.getInstance().start();

       //开启推送短信的线程
       log.info("-----------开启日推送短信的线程的执行---------------------");
        PushSmsThread.getInstance().start();
    }
}
