package com.linksteady.operate.thread;

import com.linksteady.operate.dao.PushLogMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.push.PushMessageService;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.PushLargeListService;
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
        DailyPropertiesService dailyPropertiesService=(DailyPropertiesService) SpringContextUtils.getBean("dailyPropertiesServiceImpl");
        PushLogMapper pushLogMapper = (PushLogMapper) SpringContextUtils.getBean("pushLogMapper");
        log.info(">>>[batch]批量通道-待发送的推送列表进行监控");
        while (true) {
            DailyProperties dailyProperties= dailyPropertiesService.getDailyProperties();
            if (null != dailyProperties && "N".equals(dailyProperties.getPushFlag())) {
                log.info(">>>[batch]批量通道-推送服务已通过配置停止");
                try {
                    //每隔1分钟执行一次
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            int currentHour = LocalDateTime.now().getHour();
            int count = pushLargeListService.getPushListCount(currentHour);
            if (count == 0) {
                log.info(">>>[batch]当前时间没有可推送的名单");
            } else {
                AtomicInteger atomicInteger = new AtomicInteger();
                log.info(">>>[batch]当前时间获取到共{}条短信内容待推送", count);
                Long startTime = System.currentTimeMillis();
                Long maxPushId = pushLargeListService.getMaxPushId(currentHour);
                //获取要推送的短信列表
                List<String> smsContent = pushLargeListService.getSmsContent(currentHour);
                // 迭代发送每种短信的用户名单
                for (String sms : smsContent) {
                    // 根据短信内容和当前时间获取数据总量
                    int total = pushLargeListService.getPushListCountBySms(currentHour, sms);
                    log.info(">>>[batch]短信内容：{},推送用户数：{}", sms, total);
                    int pageSize = PAGE_SIZE;
                    int pageNum = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
                    for (int i = 0; i < pageNum; i++) {
                        int start = i * pageSize;
                        int end = (i + 1) * pageSize - 1;
                        end = end > total ? total : end;
                        List<PushListLager> pushList = pushLargeListService.getPushList(currentHour, sms, start, end);
                        int pushCount = pushMessageService.batchPush(sms, pushList);
                        atomicInteger.addAndGet(pushCount);
                        log.info(">>>[batch]当前推送第{}页名单", i);
                    }
                }

                pushLargeListService.updatePushState(smsContent, maxPushId,currentHour);
                log.info(">>>[batch]结束更新推送状态");
                Long endTime = System.currentTimeMillis();
                log.info(">>>[batch]本次推送完毕，耗时{}秒", (endTime - startTime)/1000);

                int repeatUserCount = atomicInteger.get();
                int successNum = count - repeatUserCount;

                PushLog pushLog = new PushLog();
                pushLog.setLogType("1");
                pushLog.setLogContent("成功触达" + successNum + "人");
                pushLog.setUserCount((long) successNum);
                pushLog.setLogDate(new Date());
                pushLogMapper.insertPushLog(pushLog);

                //写入到重复推送日志中
                PushLog repeatLog = new PushLog();
                repeatLog.setLogType("0");
                repeatLog.setLogContent("重复推送" + repeatUserCount + "人");
                repeatLog.setUserCount((long) repeatUserCount);
                repeatLog.setLogDate(new Date());
                pushLogMapper.insertPushLog(repeatLog);

                log.info(">>>[batch]结果已写入日志表");
            }
            //每隔5分钟执行一次
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(">>>[batch]开始下一次推送");
        }
    }
}
