package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.PushLogMapper;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.service.PushLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class PushLogServiceImpl implements PushLogService, InitializingBean {

    @Autowired
    PushLogMapper pushLogMapper;

    private static final int queen_capacity=1000000;

    private BlockingDeque<PushLog> queue=new LinkedBlockingDeque<>(queen_capacity);

    @Override
    public void afterPropertiesSet() {
          consumeLog();
    }

    /**
     * 消息的保存
     */
    @Override
    public void insertPushLog(PushLog pushLog) {
        //add 非阻塞 如果队列已满 抛异常
        queue.add(pushLog);
    }

    /**
     * 消息的处理
     */
    @Override
    public void consumeLog() {

        Runnable messageConsumer=new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    log.debug("--------------对错误消息队列进行持续监控----------------------------");
                    try {
                        //take 队列为空时，阻塞到取到数据
                        PushLog pushLog=queue.take();
                        log.debug("获取到消息{}",pushLog.getLogContent());

                        //存入数据库
                        pushLogMapper.insertPushLog(pushLog);
                    } catch (InterruptedException e) {
                        log.error("消费队列异常",e);
                    }
                }
            }
        };

        Thread thread=new Thread(messageConsumer,"thread-pushlog-consumer");
        thread.start();
    }

    @Override
    public List<PushLog> getPushLogList(int day) {
        return pushLogMapper.getList(day);
    }
}
