package com.linksteady.operate.push.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.PushLargeListMapper;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.domain.PushListLarge;
import com.linksteady.operate.push.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private PushLargeListMapper pushLargeListMapper;

    @Override
    public int push(List<PushListInfo> list) {
        int result = -1;

        //推送的列表
        List<PushListInfo> userlist = Lists.newArrayList();
        SetOperations<String,String> operations=redisTemplate.opsForSet();
        String currDay= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        boolean repeatFlag;
        int repeatUserCount=0;

        for (PushListInfo pushListInfo : list) {
            repeatFlag = false;
            //开启了防骚扰拦截
            repeatFlag=operations.isMember("pushList",pushListInfo.getUserPhone());
            if (repeatFlag) {
                repeatUserCount+=1;
                log.error("用户{}存在被重复触达的风险！！", pushListInfo.getUserPhone());
                pushListInfo.setPushStatus("C");
                pushListInfo.setCallbackCode("-1");
                pushListInfo.setPushDate(new Date());
            }else
            {
                //模拟发送
                log.info("模拟触达给{}:{}", pushListInfo.getUserPhone(), pushListInfo.getPushContent());

                pushListInfo.setPushStatus("S");
                pushListInfo.setCallbackCode(String.valueOf(result));
                pushListInfo.setPushDate(new Date());

                //已发送用户存入redis
                operations.add("pushList",pushListInfo.getUserPhone());
                operations.add("pushList"+currDay,pushListInfo.getUserPhone());
            }
            userlist.add(pushListInfo);
        }

        int userSize = userlist.size();
        if (userSize > 0) {
            //更新推送状态
            pushListMapper.updateSendStatus(userlist);
        }

        return repeatUserCount;
    }

    @Override
    public int push(String uid, String messageContent) {
        //print
        return 0;
    }

    @Override
    public int batchPush(String messageContent, List<PushListLarge> pushList) {
        //要进行推送的目标人群
        List<String> targetMobileList=Lists.newArrayList();
        // 需要更新状态为骚扰拦截的用户ID
        List<Long> repeatList=Lists.newArrayList();
        //标记为成功状态的用户ID
        List<Long> successList=Lists.newArrayList();

        SetOperations<String,String> operations=redisTemplate.opsForSet();
        String currDay= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        int repeatUserCount=0;
        boolean repeatFlag;
        for(PushListLarge pushListLarge:pushList)
        {
            //判断是否会存在骚扰拦截
            repeatFlag=operations.isMember("pushList",pushListLarge.getUserPhone());
            if(repeatFlag)
            {
                repeatUserCount+=1;
                repeatList.add(pushListLarge.getPushId());
            }else
            {
                successList.add(pushListLarge.getPushId());
                targetMobileList.add(pushListLarge.getUserPhone());
                //加入到redis中去
                operations.add("pushList",pushListLarge.getUserPhone());
                operations.add("pushList"+currDay,pushListLarge.getUserPhone());
            }
        }

        if(targetMobileList.size()>0)
        {
            log.info("当前手机号：{},要推送的用户{}",messageContent,targetMobileList.stream().collect(Collectors.joining(",")));
        }

        log.debug("被拦截的记录数{}",repeatList.size());
        log.debug("成功的记录数{}",successList.size());
        //更新拦截记录的状态
        for (int limit = 100, skip = 0; skip < repeatList.size(); skip = skip + limit) {
            log.debug("拦截ID:{}",repeatList.stream().skip(skip).limit(limit).map(p->String.valueOf(p)).collect(Collectors.joining(",")));
            pushLargeListMapper.updatePushState(repeatList.stream().skip(skip).limit(limit).collect(Collectors.toList()),"C");
        }

        //更新成功记录的状态
        for (int limit = 100, skip = 0; skip < successList.size(); skip = skip + limit) {
            pushLargeListMapper.updatePushState(successList.stream().skip(skip).limit(limit).collect(Collectors.toList()),"S");
        }
        return repeatUserCount;
    }
}