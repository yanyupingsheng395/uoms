package com.linksteady.operate.push.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.push.PushMessageService;
import com.linksteady.operate.sms.montnets.domain.Message;
import com.linksteady.operate.sms.montnets.send.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 推送消息 默认为模拟打印log
 */
@Service
@Slf4j
public class PushDefaultServiceImpl implements PushMessageService {

    @Autowired
    DailyProperties dailyProperties;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DailyPushMapper dailyPushMapper;

    @Override
    public void push(List<DailyPushInfo> list) {
        int result=-1;

        //用户的列表
        List<DailyPushInfo> userlist= Lists.newArrayList();

        ValueOperations<String,String> operations=redisTemplate.opsForValue();
        //获取防骚扰拦截的时间
        int timeout=dailyProperties.getRepeatPushDays()*86400;

        boolean repeatFlag=false;

        for(DailyPushInfo dailyPushInfo:list)
        {
            repeatFlag=false;
            ////开启了防骚扰拦截
            if("Y".equals(dailyProperties.getRepeatPush()))
            {
                String value=operations.get("PUSH_"+dailyPushInfo.getUserPhone());
                if(null!=value&&!"".equals(value))
                {
                    repeatFlag=true;
                    log.error("用户{}存在被重复触达的风险！！",dailyPushInfo.getUserPhone());

                    dailyPushInfo.setPushStatus("C");
                    dailyPushInfo.setPushCallbackCode("-1");
                    dailyPushInfo.setPushDate(new Date());
                }
            }

            if(!repeatFlag)
            {
                //模拟发送
                log.info("模拟触达给{}:{}",dailyPushInfo.getUserId(),dailyPushInfo.getSmsContent());

                dailyPushInfo.setPushStatus("S");
                dailyPushInfo.setPushCallbackCode(String.valueOf(result));
                dailyPushInfo.setPushDate(new Date());

                //如何开启了放骚扰，则需要将推送结果存入redis
                if("Y".equals(dailyProperties.getRepeatPush()))
                {
                    //已发送用户存入redis
                    operations.set("PUSH_"+dailyPushInfo.getUserPhone(),dailyPushInfo.getUserPhone(),timeout);
                }

            }
            userlist.add(dailyPushInfo);

        }
        if(userlist.size()>0)
        {
            dailyPushMapper.updateSendStatus(userlist);
        }

    }
}


