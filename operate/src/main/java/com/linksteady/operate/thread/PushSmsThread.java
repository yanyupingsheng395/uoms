package com.linksteady.operate.thread;

import com.google.common.collect.Lists;
import com.linksteady.operate.common.util.SpringContextUtils;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.service.impl.DailyPushServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PushSmsThread {
    private static PushSmsThread instance = new PushSmsThread();
    public static PushSmsThread getInstance(){
        return instance;
    }

    Thread pushSmsThread=null;

    /**
     * 推送短信
     */
    @SneakyThrows
    public void start()
    {

        pushSmsThread=new Thread(new Runnable() {
            @Override
            public void run() {
                DailyPushServiceImpl dailyPushService=(DailyPushServiceImpl)SpringContextUtils.getBean("dailyPushServiceImpl");
                //存储最终发送状态的list
                List<DailyPushInfo> failedResult= Lists.newArrayList();
                List<DailyPushInfo> successResult= Lists.newArrayList();

                int size=100;
                log.info("---------对待发送的推送列表进行监控----------------");
                while(true)
                {
                    failedResult.clear();
                    successResult.clear();

                    //获取所有头处于doing状态  发送列表处于P 且在当前推荐时间段内发送的推送列表
                    List<DailyPushInfo> list=dailyPushService.getSendSmsList();

                    for(DailyPushInfo dailyPushInfo:list)
                    {
                        //调用发短信服务
                        int result=dailyPushService.sendMessage(dailyPushInfo.getUserIdentify(),dailyPushInfo.getSmsContent());

                        if(result<=5)
                        {   //失败
                            failedResult.add(dailyPushInfo);
                        }else
                        {   //成功
                            successResult.add(dailyPushInfo);
                        }
                    }

                    //批量更新发送状态为失败的记录
                    if(failedResult.size()>0)
                    {
                        if(failedResult.size()<=size)
                        {
                            dailyPushService.updateSendStatus(failedResult,"F");
                        }else //分页保存
                        {
                            //总记录数
                            int subCount = failedResult.size();
                            //页数
                            int subPageTotal = (subCount / size) + ((subCount % size > 0) ? 1 : 0);
                            // 根据页码取数据
                            for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
                                // 分页计算
                                int fromIndex = i * size;
                                int toIndex = ((i == len) ? subCount : ((i + 1) * size));
                                List<DailyPushInfo> temp = failedResult.subList(fromIndex, toIndex);

                                //进行一次保存
                                dailyPushService.updateSendStatus(failedResult,"F");
                            }
                        }
                    }

                    if(successResult.size()>0)
                    {
                        if(successResult.size()<=size)
                        {
                            dailyPushService.updateSendStatus(successResult,"S");
                        }else //分页保存
                        {
                            //总记录数
                            int subCount = successResult.size();
                            //页数
                            int subPageTotal = (subCount / size) + ((subCount % size > 0) ? 1 : 0);
                            // 根据页码取数据
                            for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
                                // 分页计算
                                int fromIndex = i * size;
                                int toIndex = ((i == len) ? subCount : ((i + 1) * size));
                                List<DailyPushInfo> temp = successResult.subList(fromIndex, toIndex);

                                //进行一次保存
                                dailyPushService.updateSendStatus(successResult,"S");
                            }
                        }
                    }

                    //更新主记录的状态 (对于已全部完成发送的更改头表状态为done)
                    dailyPushService.updateHeaderToDone();

                    //对最近三日的触达情况进行汇总、更新
                    dailyPushService.updateHeaderSendStatis();
                    //每隔10分钟执行一次
                    try {
                        TimeUnit.MINUTES.sleep(5);
                        log.info("---------持续对待发送的短信列表进行监控----------------");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        pushSmsThread.setName("sendSmsTheread");
        pushSmsThread.setDaemon(true);
        pushSmsThread.start();
    }


}
