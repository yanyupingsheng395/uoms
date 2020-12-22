package com.linksteady.qywx.config;

import com.linksteady.qywx.thread.SyncQywxMsgExecThread;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class startThreadCommand implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Thread thread=new Thread(new SyncQywxMsgExecThread(),"同步消息执行结果任务");
        thread.start();
    }
}
