package com.linksteady.operate.push.impl;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.push.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 推送消息 腾讯公众号消息接口实现
 */
@Service
@Slf4j
public class PushWxMessageServiceImpl implements PushMessageService {

    @Autowired
    DailyProperties dailyProperties;

    @Autowired
    RedisTemplate<String,String> redisTemplate;


    @Override
    public void push(List<PushListInfo> list) {

    }

    @Override
    public int push(String uid, String messageContent) {
        return 0;
    }
}
