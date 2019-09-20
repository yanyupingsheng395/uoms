//package com.linksteady.operate.thread;
//
//import com.linksteady.operate.service.impl.DailyPushServiceImpl;
//import com.linksteady.operate.util.SpringContextUtils;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.LinkedBlockingQueue;
//
///**
// * 生成每日运营推送名单的多线程类
// */
//@Slf4j
//public class GenPushListThread {
//
//    private static GenPushListThread instance = new GenPushListThread();
//    public static GenPushListThread getInstance(){
//        return instance;
//    }
//    /**
//     * 日运营的头ID的队列
//     */
//    private LinkedBlockingQueue<String> triggerQueue=new LinkedBlockingQueue<String>();
//
//
//    /**
//     * 将待生成名单的HeaderID放入队列中
//     * @param headerId
//     */
//     public static void generatePushList(String headerId)
//     {
//         log.info("日推送收到headerId:{},待生成名单!",headerId);
//         getInstance().triggerQueue.add(headerId);
//     }
//
//     Thread generatePushThread=null;
//    /**
//     * 生成推送名单
//     */
//    @SneakyThrows
//    public void start()
//    {
//        generatePushThread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true)
//                {
//                    String headerId="";
//                    try {
//                        //从queue中取出header_id
//                        headerId=getInstance().triggerQueue.take();
//                        log.info("日推送从队列中取到headerId:{},待生成名单!",headerId);
//
//                        //todo 进行一次预校验 确保发送都能成功
//
//                        //针对推送名单 填充消息模板 生成文案
//                        DailyPushServiceImpl dailyPushService=(DailyPushServiceImpl) SpringContextUtils.getBean("dailyPushServiceImpl");
//                        dailyPushService.generatePushList(headerId);
//
//                        log.info("日推送headerId:{},的推送名单已生成,即将开始推送",headerId);
//                    } catch (Exception e) {
//                        //异常消息上报
//                        log.error("日运营{}生成推送名单报错{}，",headerId,e);
//                    }
//                }
//            }
//        });
//
//        generatePushThread.setDaemon(true);
//        generatePushThread.setName("GenPushListThread");
//        generatePushThread.start();
//    }
//}
