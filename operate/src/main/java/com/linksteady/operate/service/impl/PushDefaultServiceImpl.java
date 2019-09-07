package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.service.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
         //开启了防骚扰拦截
         if("Y".equals(dailyProperties.getRepeatPush()))
         {
             ValueOperations<String,String> operations=redisTemplate.opsForValue();
              //获取防骚扰拦截的时间
             int timeout=dailyProperties.getRepeatPushDays()*86400;

             //被拦截用户的列表
             List<DailyPushInfo> cList= Lists.newArrayList();
             //正常触达用户
             List<DailyPushInfo> nList= Lists.newArrayList();

             for(DailyPushInfo dailyPushInfo:list)
             {
                 String value=operations.get("PUSH_"+dailyPushInfo.getUserId());
                 if(null!=value&&!"".equals(value))
                 {
                     log.error("用户{}存在被重复触达的风险！！",dailyPushInfo.getUserId());
                     cList.add(dailyPushInfo);
                 }else
                 {
                     nList.add(dailyPushInfo);
                     //已发送用户存入redis
                     operations.set("PUSH_"+dailyPushInfo.getUserId(),dailyPushInfo.getUserId(),timeout);
                 }
             }

             for(DailyPushInfo dailyPushInfo:nList)
             {
                 log.info("模拟触达给{}:{}",dailyPushInfo.getUserId(),dailyPushInfo.getSmsContent());
             }

             if(nList.size()>0)
             {
                 //模拟打印实现，状态直接为成功
                 dailyPushMapper.updateSendStatus(nList,"S");
             }

             if(cList.size()>0)
             {
                 //标记为已被防骚扰拦截
                 dailyPushMapper.updateSendStatus(cList,"C");
             }

         }else
         {
             for(DailyPushInfo dailyPushInfo:list)
             {
                 log.info("模拟触达给{}:{}",dailyPushInfo.getUserId(),dailyPushInfo.getSmsContent());
             }

             if(list.size()>0)
             {
                 dailyPushMapper.updateSendStatus(list,"S");
             }
         }

    }
}
