package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.service.RedisMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Map;

@Service
public class RedisMessageServiceImpl implements RedisMessageService {

    private static final String PUSH_TEST_SMS_CHANNEL="testsmschannel";

    private static final String START_STOP_CHANNEL="startorstopchannel";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PushListMapper pushListMapper;

    /**
     * 接收推送心跳信息
     * @param pushHeartBeatMessage
     */
    @Override
    public void receivePushHeartBeat(String pushHeartBeatMessage) {
        HeartBeatInfo temp=JSON.parseObject(pushHeartBeatMessage, HeartBeatInfo.class);
        HeartBeatInfo heartBeatInfo=HeartBeatInfo.getInstance();

        if(null!=temp)
        {
            if(null!=temp.getLastPushDate())
            {
                heartBeatInfo.setLastPushDate(temp.getLastPushDate());
            }
            if(null!=temp.getLastBatchPushDate())
            {
                heartBeatInfo.setLastBatchPushDate(temp.getLastBatchPushDate());
            }
            if(null!=temp.getLastPurgeDate())
            {
                heartBeatInfo.setLastPurgeDate(temp.getLastPurgeDate());
            }
            if(null!=temp.getLastRptDate())
            {
                heartBeatInfo.setLastRptDate(temp.getLastRptDate());
            }
            if(null!=temp.getLastMoDate())
            {
                heartBeatInfo.setLastMoDate(temp.getLastMoDate());
            }
        }

    }

    /**
     * 测试的短信放入redis通道
     * @param phoneNum
     * @param smsContent
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPhoneMessage(String phoneNum,String smsContent) {
        Map<String,String> temp= Maps.newHashMap();

        //写入uo_op_push_list
        PushListInfo pushListInfo=new PushListInfo();
        pushListInfo.setUserPhone(phoneNum);
        pushListInfo.setPushContent(smsContent);
        pushListInfo.setPushPeriod(String.valueOf(LocalTime.now().getHour()));

        pushListMapper.insertTestMsg(pushListInfo);
        temp.put("pushId",String.valueOf(pushListInfo.getPushId()));
        temp.put("phoneNum",phoneNum);
        temp.put("smsContent",smsContent);

        stringRedisTemplate.convertAndSend(PUSH_TEST_SMS_CHANNEL,JSON.toJSONString(temp));
    }

    @Override
    public void sendPushSingal(HeartBeatInfo heartBeatInfo) {
        //发送推送的心跳
        stringRedisTemplate.convertAndSend(START_STOP_CHANNEL,JSON.toJSONString(heartBeatInfo));
    }
}
