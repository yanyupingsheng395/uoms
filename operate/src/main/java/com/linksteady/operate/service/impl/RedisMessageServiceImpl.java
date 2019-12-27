package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.operate.service.RedisMessageService;
import com.linksteady.operate.thread.MonitorThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RedisMessageServiceImpl implements RedisMessageService {

    private static final String PUSH_TEST_SMS_CHANNEL="testsmschannel";
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 接收推送心跳信息
     * @param pushHeartBeatMessage
     */
    @Override
    public void receivePushHeartBeat(String pushHeartBeatMessage) {
        HeartBeatInfo heartBeatInfo=JSON.parseObject(pushHeartBeatMessage, HeartBeatInfo.class);
        MonitorThread monitorThread=MonitorThread.getInstance();

        if(null!=heartBeatInfo)
        {
            if(null!=heartBeatInfo.getLastPushDate())
            {
                monitorThread.setLastPushDate(heartBeatInfo.getLastPushDate());
            }
            if(null!=heartBeatInfo.getLastBatchPushDate())
            {
                monitorThread.setLastBatchPushDate(heartBeatInfo.getLastBatchPushDate());
            }
            if(null!=heartBeatInfo.getLastPurgeDate())
            {
                monitorThread.setLastPurgeDate(heartBeatInfo.getLastPurgeDate());
            }
        }

    }

    /**
     * 测试的短信放入redis通道
     * @param phoneNum
     * @param smsContent
     */
    @Override
    public void sendPhoneMessage(String phoneNum,String smsContent) {
        Map<String,String> temp= Maps.newHashMap();
        temp.put("phoneNum",phoneNum);
        temp.put("smsContent",smsContent);
        stringRedisTemplate.convertAndSend(PUSH_TEST_SMS_CHANNEL,temp);
    }
}
