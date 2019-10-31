package com.linksteady.operate.thread;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.PushLogService;
import com.linksteady.operate.service.impl.PushLogServiceImpl;
import com.linksteady.operate.util.SpringContextUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 清空redis中防重复推送SET中已过期的用户名单
 */
@Slf4j
public class PurgeThread {
    private static PurgeThread instance = new PurgeThread();

    public static PurgeThread getInstance() {
        return instance;
    }

    Thread purgePushListThread = null;

    /**
     * 清空的线程
     */
    @SneakyThrows
    public void start() {
        purgePushListThread = new Thread(() -> {
           // 推送配置
            DailyProperties dailyProperties=(DailyProperties) SpringContextUtils.getBean("dailyProperties");

            // 写日志service
            PushLogService pushLogService=(PushLogServiceImpl) SpringContextUtils.getBean("pushLogServiceImpl");

            RedisTemplate<String,String> redisTemplate = (RedisTemplate) SpringContextUtils.getBean("redisTemplate");
            SetOperations<String,String> operations=redisTemplate.opsForSet();

            //系统启动之后延迟5分钟执行
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true)
            {
                MonitorThread.getInstance().setLastPurgeDate(LocalDateTime.now());

                //获取防重复的日期
                int days=dailyProperties.getRepeatPushDays();

                //获取当前日期
                LocalDate today = LocalDate.now();
                LocalDate begin=today.minusDays(days);


                //开始循环 (固定向前推5天)
                Long expireCount=Stream.iterate(begin,d->d.minusDays(1)).limit(5).mapToLong(f->{
                    String key="pushList"+f.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    //判断当前key在redis中是否存在
                    boolean isExist=redisTemplate.hasKey(key);

                    if(isExist)
                    {
                        Long expireSize=operations.size(key);
                        Long beforeExpireSize=operations.size("pushList");

                        Long afterExpireSize=operations.differenceAndStore("pushList","pushList",key);

                        redisTemplate.delete(key);
                        log.info("已推送对象失效处理：对key={}中的{}个用户进行失效处理，失效前共有{}，失效后存在{}个。",key,expireSize,beforeExpireSize,afterExpireSize);
                        return expireSize;
                    }else
                    {
                        return 0L;
                    }
                }).sum();

                //写入日志
                PushLog pushLog = new PushLog();
                pushLog.setLogType("2");
                pushLog.setLogContent("防骚扰触达-失效监测运行");
                pushLog.setUserCount(expireCount);
                pushLog.setLogDate(new Date());
                pushLogService.insertPushLog(pushLog);

                //休眠 获取下一天的6:30 计算和当前时间的时间差，就是线程需要休眠的时间
                LocalDateTime next=LocalDateTime.of(today.plusDays(1), LocalTime.of(6,30));
                Long minutes=ChronoUnit.MINUTES.between(LocalDateTime.now(),next);
                try {
                    TimeUnit.MINUTES.sleep(minutes);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        purgePushListThread.setName("purge_push_list_thread");
        purgePushListThread.setDaemon(true);
        purgePushListThread.start();
    }
}
