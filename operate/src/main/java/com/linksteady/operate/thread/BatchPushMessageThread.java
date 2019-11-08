package com.linksteady.operate.thread;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListLarge;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.push.PushMessageService;
import com.linksteady.operate.service.PushLargeListService;
import com.linksteady.operate.service.PushLogService;
import com.linksteady.operate.service.impl.PushLogServiceImpl;
import com.linksteady.operate.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 双11活动批量发
 *
 * @author hxcao
 * @date 2019-10-23
 */
@Slf4j
public class BatchPushMessageThread extends Thread {

    /**
     * 批量接口的上限是1000
     */
    private final int PAGE_SIZE = 998;

    @Override
    public void run() {
        PushLargeListService pushLargeListService = (PushLargeListService) SpringContextUtils.getBean(PushLargeListService.class);
        PushMessageService pushMessageService = (PushMessageService) SpringContextUtils.getBean("pushMessageServiceImpl");
        DailyProperties dailyProperties=(DailyProperties) SpringContextUtils.getBean("dailyProperties");
        PushLogService pushLogService=(PushLogServiceImpl) SpringContextUtils.getBean("pushLogServiceImpl");
        //实际推送人数
        AtomicInteger actualCount = new AtomicInteger();
        //被拦截人数
        AtomicInteger repeatCount = new AtomicInteger();
        log.info(">>>[batch]批量通道-待发送的推送列表进行监控");
        while (true) {
            MonitorThread.getInstance().setLastBatchPushDate(LocalDateTime.now());
            //每隔5分钟执行一次
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            actualCount.set(0);
            repeatCount.set(0);
            Long startTime = System.currentTimeMillis();

            int currentHour = LocalDateTime.now().getHour();
            int count = pushLargeListService.getPushLargeListCount(currentHour);
            if (count == 0) {
                log.info(">>>[batch]当前时间没有可推送的名单");
            } else {

                log.info(">>>[batch]当前时间获取到共{}条短信内容待推送", count);

                //获取要推送的短信列表
                List<String> smsContent = pushLargeListService.getSmsContentList(currentHour);
                // 迭代发送每种短信的用户名单
                for (String sms : smsContent) {

                    if (null != dailyProperties && "N".equals(dailyProperties.getPushFlag())) {
                        log.info(">>>[batch]批量通道-推送服务已通过配置停止");
                        break;
                    }else
                    {
                        // 根据短信内容和当前时间获取数据总量
                        int total = pushLargeListService.getPushListCountBySms(currentHour, sms);
                        int pageSize = PAGE_SIZE;
                        int pageNum = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
                        log.info(">>>[batch]短信内容：{},推送用户数：{},页数：{}", sms, total,pageNum);
                        for (int i = 0; i < pageNum; i++) {
                            //不是常规的分页，因为batchPush方法中将数据的状态进行了修改，因此每次都从头去拿
                            List<PushListLarge> pushList = pushLargeListService.getPushLargeList(currentHour, sms, 0, PAGE_SIZE);
                            int pushCount = pushMessageService.batchPush(sms, pushList);
                            repeatCount.addAndGet(pushCount);
                            actualCount.addAndGet(pushList.size());
                        }
                    }
                }
                Long endTime = System.currentTimeMillis();
                log.info(">>>[batch]本次推送完毕，耗时{}秒", (endTime - startTime)/1000);

                if(actualCount.intValue()>0){
                    int successNum = actualCount.intValue() - repeatCount.intValue();
                    PushLog pushLog = new PushLog();
                    pushLog.setLogType("1");
                    pushLog.setLogContent("[batch]成功触达" + successNum + "人");
                    pushLog.setUserCount((long) successNum);
                    pushLog.setLogDate(new Date());
                    pushLogService.insertPushLog(pushLog);

                    //写入到重复推送日志中
                    PushLog repeatLog = new PushLog();
                    repeatLog.setLogType("0");
                    repeatLog.setLogContent("[batch]防重复推送拦截" + repeatCount.longValue() + "人");
                    repeatLog.setUserCount(repeatCount.longValue());
                    repeatLog.setLogDate(new Date());
                    pushLogService.insertPushLog(repeatLog);
                    log.info(">>>[batch]结果已写入日志表");
                }
            }
            Long endTime = System.currentTimeMillis();
            log.info(">>>[batch]已触达完毕，应推送用户数：{}人，实际推送{}人,拦截{}人，共耗时：{}毫秒", count,actualCount.intValue(),repeatCount.intValue(), endTime - startTime);
            log.info(">>>[batch]开始下一次推送");
        }
    }
}
