package com.linksteady.operate.thread;

import com.linksteady.operate.common.util.SpringContextUtils;
import com.linksteady.operate.service.impl.DailyPushServiceImpl;
import com.linksteady.operate.service.impl.DailyServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 生成每日运营推送名单的多线程类
 */
@Slf4j
public class PushListThread {

    private static PushListThread instance = new PushListThread();
    public static PushListThread getInstance(){
        return instance;
    }
    /**
     * 日运营的头ID的队列
     */
    private LinkedBlockingQueue<String> triggerQueue=new LinkedBlockingQueue<String>();


    /**
     * 将待生成名单的HeaderID放入队列中
     * @param headerId
     */
     public static void generatePushList(String headerId)
     {
         log.info("日推送收到heaerId:{},待生成名单!",headerId);
         getInstance().triggerQueue.add(headerId);
     }

     Thread generatePushThread=null;
    /**
     * 生成推送名单
     */
    @SneakyThrows
    public void start()
    {
        generatePushThread=new Thread(new Runnable() {
            @Override
            public void run() {
               //对queue中取出header_id
                while(true)
                {
                    try {
                        String headerId=getInstance().triggerQueue.take();
                        log.info("日推送从队列中取到heaerId:{},待生成名单!",headerId);

                        //将此headerId对应的用户写入到推送明细表中去
                        DailyPushServiceImpl dailyPushService=(DailyPushServiceImpl)SpringContextUtils.getBean("dailyPushServiceImpl");

                        dailyPushService.generatePushList(headerId);

                        //更新当前header_Id对应数据的状态为ready_push
                        DailyServiceImpl dailyService=(DailyServiceImpl)SpringContextUtils.getBean("dailyServiceImpl");
                        dailyService.updateStatus(headerId,"ready_push");

                        log.info("日推送heaerId:{},的推送名单已生成",headerId);
                    } catch (InterruptedException e) {
                        //异常日志上报
                        e.printStackTrace();
                    }
                }
            }
        });

        generatePushThread.setDaemon(true);
        generatePushThread.setName("generate op daily push list");
        generatePushThread.start();
    }
}
