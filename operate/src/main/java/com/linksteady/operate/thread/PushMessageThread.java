package com.linksteady.operate.thread;

import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.service.impl.DailyPushServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 获取到待推送的列表 进行消息推送的线程类
 */
@Slf4j
public class PushMessageThread {
    private static PushMessageThread instance = new PushMessageThread();
    public static PushMessageThread getInstance(){
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
                DailyProperties dailyProperties=(DailyProperties)SpringContextUtils.getBean("dailyProperties");

                int size=100;
                log.info("---------对待发送的推送列表进行监控----------------");
                while(true)
                {
                    if(null!=dailyProperties&&"N".equals(dailyProperties.getPushFlag()))
                    {
                        log.info("----------------推送服务已通过配置停止---------------");
                        try {
                            //每隔1分钟执行一次
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;

                    }

                    //每隔5分钟执行一次
                    try {
                        TimeUnit.MINUTES.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    log.info("---------开始下一次推送----------------");
                    //查询 所有头处于doing状态  发送列表处于P 的最大的daily_detail_id (此处是为了防止并发导致的分页不准确)
                    int dailyDetailId=dailyPushService.getPrePushUserMaxId();

                    //获取所有头处于doing状态  发送列表处于P 且在当前推荐时间段内发送的推送列表 考虑分页
                    int count=dailyPushService.getPrePushUserCount(dailyDetailId);
                    List<DailyPushInfo> list=null;
                    if(count<=size)
                    {
                        //获取推送列表
                        list=dailyPushService.getPrePushUserList(dailyDetailId,1,count);
                        dailyPushService.push(list);
                    }else
                    {
                        //分页
                        int pageSize=count%size==0?count/size:(count/size+1);
                        for(int i=0;i<pageSize;i++)
                        {
                            list=dailyPushService.getPrePushUserList(dailyDetailId,i*size+1,(i+1)*size);
                            dailyPushService.push(list);
                        }
                    }

                    //更新主记录的状态 (对于已全部完成发送的更改头表状态为done)
                    dailyPushService.updateHeaderToDone();

                    //对最近15日的触达情况进行汇总、更新
                    dailyPushService.updateHeaderSendStatis();

                    //对最近15日的数据统计触达率、触达人数、损失率
                    dailyPushService.updatePushStatInfo();

                    log.info("---------推送结束，持续对待发送的短信列表进行监控----------------");
                }
            }
        });

        pushSmsThread.setName("sendSmsTheread");
        pushSmsThread.setDaemon(true);
        pushSmsThread.start();
    }


}
