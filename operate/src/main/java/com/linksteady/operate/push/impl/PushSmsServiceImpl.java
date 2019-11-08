package com.linksteady.operate.push.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.PushLargeListMapper;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.dao.PushLogMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.domain.PushListLarge;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.push.PushMessageService;
import com.linksteady.operate.sms.montnets.config.ConfigManager;
import com.linksteady.operate.sms.montnets.domain.Message;
import com.linksteady.operate.sms.montnets.send.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推送消息 短信实现
 */
@Service
@Slf4j
public class PushSmsServiceImpl implements PushMessageService {

    @Value("${sms.montnets.userid}")
    private String userid;

    @Value("${sms.montnets.pwd}")
    private String pwd;

    @Value("${sms.montnets.masterIpAddress}")
    private String masterIpAddress;

    private boolean isEncryptPwd = ConfigManager.IS_ENCRYPT_PWD;

    @Autowired
    DailyProperties dailyProperties;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private PushListMapper pushListMapper;

    @Autowired
    private PushLargeListMapper pushLargeListMapper;

    @Override
    public int push(List<PushListInfo> list) {
        //发送类
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //短信接口
        Message message=null;
        //触达结果
        int result;

        //用户的列表
        List<PushListInfo> userlist= Lists.newArrayList();
        SetOperations<String,String> operations=redisTemplate.opsForSet();
        String currDay= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        boolean repeatFlag;
        int repeatUserCount=0;

        for(PushListInfo pushListInfo:list)
        {
            //判断是否会存在骚扰拦截
            repeatFlag=operations.isMember("pushList",pushListInfo.getUserPhone());
            if(repeatFlag)
            {
              //  repeatFlag=true;
                repeatUserCount+=1;
                log.error("用户{}存在被重复触达的风险！！",pushListInfo.getUserPhone());

                pushListInfo.setPushStatus("C");
                pushListInfo.setCallbackCode("-1");
                pushListInfo.setPushDate(new Date());
            }else
            {
                //调用供应商短信接口进行推送
                message=new Message();
                message.setMobile(pushListInfo.getUserPhone());
                message.setContent(pushListInfo.getPushContent());
                try {
//                    result=sendSms.singleSend(message);
                    result = 0;
                    log.info("通道推送:{}-{}:返回状态码:{}",pushListInfo.getUserPhone(),pushListInfo.getPushContent(),result);
                } catch (Exception e) {
                    //此处进行错误上报
                    result=-1;
                    log.error("通道推送:{}-{}:返回状态码:{}",pushListInfo.getUserPhone(),pushListInfo.getPushContent(),result);
                }


                if(result==0)
                {
                    pushListInfo.setPushStatus("S");
                }else
                {
                    pushListInfo.setPushStatus("F");
                }
                pushListInfo.setCallbackCode(String.valueOf(result));
                pushListInfo.setPushDate(new Date());

                //已发送用户存入redis
                operations.add("pushList",pushListInfo.getUserPhone());
                operations.add("pushList"+currDay,pushListInfo.getUserPhone());
            }

            userlist.add(pushListInfo);

        }

        if(userlist.size()>0)
        {
            pushListMapper.updateSendStatus(userlist);
        }
        return repeatUserCount;
    }

    @Override
    public int push(String uid, String messageContent) {

        //发送类
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //短信接口
        Message  message=new Message();
        message.setMobile(uid);
        message.setContent(messageContent);
        //int result=sendSms.singleSend(message);
        int result=0;

        return result;
    }

    @Override
    public int batchPush(String messageContent, List<PushListLarge> pushList) {
        log.info("短信内容:{},本页内处理的数量{}",messageContent,pushList.size());
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
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
            Message message=new Message();
            message.setContent(messageContent);
            message.setMobile(targetMobileList.stream().collect(Collectors.joining(",")));
            //sendSms.batchSend(message);
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
