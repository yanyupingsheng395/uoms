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
            AtomicInteger atomicInteger = new AtomicInteger();
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

                if (null != dailyProperties && "N".equals(dailyProperties.getPushFlag())) {
                    log.info("[每日运营]推送服务已通过配置停止");
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

                log.info("[每日运营]开始下一次推送");
                atomicInteger.set(0);
                Long startTime = System.currentTimeMillis();
                //获取当前所在的小时
                int currHour = LocalDateTime.now().getHour();
                //查询 所有待推送状态 的最大的push_id (此处是为了防止并发导致的分页不准确)
                int maxPushId = pushListService.getPendingPushMaxId(currHour);

                //获取所有当前推荐时间段内发送的推送列表 考虑分页
                int count = pushListService.getPendingPushCount(maxPushId, currHour);
                log.info("当前时段{},当前选择的触达方式为{}，本批次触达人数:{}", currHour, dailyProperties.getPushType(), count);
                int repeatUserCount =0;
                List<PushListInfo> list;
                if (count <= size) {
                    //获取推送列表
                    list = pushListService.getPendingPushList(maxPushId, 1, count, currHour);
                    repeatUserCount = pushMessageService.push(list);
                    atomicInteger.addAndGet(repeatUserCount);
                } else {
                    //分页
                    int pageSize = count % size == 0 ? count / size : (count / size + 1);
                    for (int i = 0; i < pageSize; i++) {
                        list = pushListService.getPendingPushList(maxPushId, i * size + 1, (i + 1) * size, currHour);
                        repeatUserCount =pushMessageService.push(list);
                        atomicInteger.addAndGet(repeatUserCount);
                    }
                }
                //更新这一批数据的IS_PUSH字段
                pushListService.updateIsPush(maxPushId, currHour);

                if (count != 0) {
                    //写入到触达日志中
                    repeatUserCount = atomicInteger.get();
                    int successNum = count - repeatUserCount;
                    PushLog pushLog = new PushLog();
                    pushLog.setLogType("1");
                    pushLog.setLogContent("成功触达" + successNum + "人");
                    pushLog.setUserCount((long) successNum);
                    pushLog.setLogDate(new Date());
                    pushLogService.insertPushLog(pushLog);

                    //写入到重复推送日志中
                    PushLog repeatLog = new PushLog();
                    repeatLog.setLogType("0");
                    repeatLog.setLogContent("重复推送" + repeatUserCount + "人");
                    repeatLog.setUserCount((long) repeatUserCount);
                    repeatLog.setLogDate(new Date());
                    pushLogService.insertPushLog(repeatLog);
                    log.info(">>>[每日运营]结果已写入日志表");
                }
                log.info("[每日运营]推送结束，持续对待发送的短信列表进行监控");
                Long endTime = System.currentTimeMillis();
                log.info(">>>[每日运营]已触达完毕，用户数：{}人，共耗时：{}毫秒", count, endTime - startTime);

            }
        });

        pushSmsThread.setName("daily_sms_send_thread");
        pushSmsThread.setDaemon(true);
        pushSmsThread.start();
    }
}
