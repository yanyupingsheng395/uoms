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
 * 推送消息 阿里云短信实现
 */
@Service
@Slf4j
public class PushAliSmsServiceImpl implements PushMessageService {

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
                 String value=operations.get("PUSH_"+dailyPushInfo.getUserPhone());
                 if(null!=value&&!"".equals(value))
                 {
                     log.error("用户{}存在被重复触达的风险！！",dailyPushInfo.getUserPhone());
                     cList.add(dailyPushInfo);
                 }else
                 {
                     nList.add(dailyPushInfo);
                     //已发送用户存入redis
                     operations.set("PUSH_"+dailyPushInfo.getUserPhone(),dailyPushInfo.getUserPhone(),timeout);
                 }
             }

             for(DailyPushInfo dailyPushInfo:nList)
             {
                //todo 调用阿里云发短信的接口
             }

//             if(nList.size()>0)
//             {
//                 //已推送 待反馈触达结果
//                 dailyPushMapper.updateSendStatus(nList,"R");
//             }
//
//             if(cList.size()>0)
//             {
//                 //标记为已被防骚扰拦截
//                 dailyPushMapper.updateSendStatus(cList,"C");
//             }
//
//         }else
//         {
//             for(DailyPushInfo dailyPushInfo:list)
//             {
//                 //todo 调用阿里云短信接口
//             }
//
//             if(list.size()>0)
//             {
//                 dailyPushMapper.updateSendStatus(list,"S");
//             }
        }

    }
}
