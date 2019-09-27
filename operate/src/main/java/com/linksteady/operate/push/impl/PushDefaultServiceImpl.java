package com.linksteady.operate.push.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.push.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 推送消息 默认为模拟打印log
 */
@Service
@Slf4j
public class PushDefaultServiceImpl implements PushMessageService {

    @Autowired
    DailyProperties dailyProperties;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PushListMapper pushListMapper;

    @Override
    public void push(List<PushListInfo> list) {
        int result = -1;

        //推送的列表
        List<PushListInfo> userlist = Lists.newArrayList();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        //获取防骚扰拦截的时间
        int timeout = dailyProperties.getRepeatPushDays() * 86400;

        boolean repeatFlag = false;

        for (PushListInfo pushListInfo : list) {
            repeatFlag = false;
            ////开启了防骚扰拦截
            if ("Y".equals(dailyProperties.getRepeatPush())) {
                String value = operations.get("PUSH_" + pushListInfo.getUserPhone());
                if (null != value && !"".equals(value)) {
                    repeatFlag = true;
                    log.error("用户{}存在被重复触达的风险！！", pushListInfo.getUserPhone());
                    pushListInfo.setPushStatus("C");
                    pushListInfo.setCallbackCode("-1");
                    pushListInfo.setPushDate(new Date());
                }
            }

            if (!repeatFlag) {
                //模拟发送
                log.info("模拟触达给{}:{}", pushListInfo.getUserPhone(), pushListInfo.getPushContent());

                pushListInfo.setPushStatus("S");
                pushListInfo.setCallbackCode(String.valueOf(result));
                pushListInfo.setPushDate(new Date());

                //如何开启了放骚扰，则需要将推送结果存入redis
                if ("Y".equals(dailyProperties.getRepeatPush())) {
                    //已发送用户存入redis
                    operations.set("PUSH_" + pushListInfo.getUserPhone(), pushListInfo.getUserPhone(), timeout);
                }
            }
            userlist.add(pushListInfo);
        }
        if (userlist.size() > 0) {
            //更新推送状态
            pushListMapper.updateSendStatus(userlist);
        }
    }

    @Override
    public int push(String uid, String messageContent) {
        //print
        return 0;
    }
}