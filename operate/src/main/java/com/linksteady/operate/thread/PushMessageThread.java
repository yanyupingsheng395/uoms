package com.linksteady.operate.thread;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.push.impl.PushMessageServiceImpl;
import com.linksteady.operate.service.PushLogService;
import com.linksteady.operate.service.impl.PushListServiceImpl;
import com.linksteady.operate.service.impl.PushLogServiceImpl;
import com.linksteady.operate.util.SpringContextUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取到待推送的列表 进行消息推送的线程类
 */
@Slf4j
public class PushMessageThread {
    private static PushMessageThread instance = new PushMessageThread();

    public static PushMessageThread getInstance() {
        return instance;
    }

    Thread pushSmsThread = null;

    /**
     * 推送短信
     */
    @SneakyThrows
    public void start() {
        pushSmsThread = new Thread(() -> {
            AtomicInteger actualCount = new AtomicInteger();
            AtomicInteger repeatCount = new AtomicInteger();
            // 推送的数据表service
            PushListServiceImpl pushListService = (PushListServiceImpl) SpringContextUtils.getBean("pushListServiceImpl");
            // 推送配置
            DailyProperties dailyProperties=(DailyProperties) SpringContextUtils.getBean("dailyProperties");

            // 推送通道service
            PushMessageServiceImpl pushMessageService = (PushMessageServiceImpl) SpringContextUtils.getBean("pushMessageServiceImpl");
            // 写日志service
            PushLogService pushLogService=(PushLogServiceImpl) SpringContextUtils.getBean("pushLogServiceImpl");


            int size = 100;
            log.info("[每日运营]对待发送的推送列表进行监控");
            while (true) {
                MonitorThread.getInstance().setLastPushDate(LocalDateTime.now());

                //每隔5分钟执行一次
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                log.info("[每日运营]开始下一次推送");
                repeatCount.set(0);
                actualCount.set(0);
                Long startTime = System.currentTimeMillis();
                //获取当前所在的小时
                int currHour = LocalDateTime.now().getHour();

                //获取所有当前推荐时间段内发送的推送列表
                int count = pushListService.getPendingPushCount(currHour);
                log.info("当前时段{},当前选择的触达方式为{}，本批次触达人数:{}", currHour, dailyProperties.getPushType(), count);
                //被拦截人数
                int repeatUserCount;

                List<PushListInfo> list;
                if (count <= size) {
                    //获取推送列表
                    list = pushListService.getPendingPushList(count, currHour);
                    if (null != dailyProperties && "N".equals(dailyProperties.getPushFlag())) {
                        log.info("[每日运营]推送服务已通过配置停止");
                    }else
                    {
                        repeatUserCount = pushMessageService.push(list);
                        repeatCount.addAndGet(repeatUserCount);
                        actualCount.addAndGet(list.size());
                    }
                } else {
                    //分页
                    int pageNum = count % size == 0 ? count / size : (count / size + 1);
                    for (int i = 0; i < pageNum; i++) {

                        //非常规的分页，每次都从头拿size数量的记录，因为在push方法中对数据的状态做了修改
                        list = pushListService.getPendingPushList(size, currHour);

                        if (null != dailyProperties && "N".equals(dailyProperties.getPushFlag())) {
                            log.info("[每日运营]推送服务已通过配置停止");
                            break;
                        }else
                        {
                            repeatUserCount =pushMessageService.push(list);
                            repeatCount.addAndGet(repeatUserCount);
                            actualCount.addAndGet(list.size());
                        }
                    }
                }

                if (actualCount.intValue() != 0) {
                    //写入到触达日志中
                    int successNum = actualCount.intValue() - repeatCount.get();
                    PushLog pushLog = new PushLog();
                    pushLog.setLogType("1");
                    pushLog.setLogContent("[daily]成功触达" + successNum + "人");
                    pushLog.setUserCount((long) successNum);
                    pushLog.setLogDate(new Date());
                    pushLogService.insertPushLog(pushLog);

                    //写入到重复推送日志中
                    PushLog repeatLog = new PushLog();
                    repeatLog.setLogType("0");
                    repeatLog.setLogContent("[daily]防重复推送拦截" + repeatCount.intValue() + "人");
                    repeatLog.setUserCount(repeatCount.longValue());
                    repeatLog.setLogDate(new Date());
                    pushLogService.insertPushLog(repeatLog);
                    log.info(">>>[每日运营]结果已写入日志表");
                }

                Long endTime = System.currentTimeMillis();
                log.info("[每日运营]推送结束，持续对待发送的短信列表进行监控");
                log.info(">>>[每日运营]已触达完毕，应推送用户数：{}人，实际推送{}人,拦截{}人，共耗时：{}毫秒", count,actualCount.intValue(),repeatCount.intValue(), endTime - startTime);

            }
        });

        pushSmsThread.setName("daily_sms_send_thread");
        pushSmsThread.setDaemon(true);
        pushSmsThread.start();
    }
}
