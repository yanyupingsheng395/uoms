package com.linksteady.operate.push.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.dao.PushLogMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.push.PushMessageService;
import com.linksteady.operate.sms.montnets.config.ConfigManager;
import com.linksteady.operate.sms.montnets.domain.Message;
import com.linksteady.operate.sms.montnets.send.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
    private PushLogMapper pushLogMapper;


    @Override
    public void push(List<PushListInfo> list) {
        //发送类
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //短信接口
        Message message=null;
        //触达结果
        int result;

        //用户的列表
        List<PushListInfo> userlist= Lists.newArrayList();

        ValueOperations<String,String> operations=redisTemplate.opsForValue();
        //获取防骚扰拦截的时间
        int timeout=dailyProperties.getRepeatPushDays()*86400;

        boolean repeatFlag;
        int repeatUserCount=0;

        for(PushListInfo pushListInfo:list)
        {
            repeatFlag=false;

            //判断是否会存在骚扰拦截
            String value=operations.get("PUSH_"+pushListInfo.getUserPhone());
            if(null!=value&&!"".equals(value))
            {
                repeatFlag=true;
                repeatUserCount+=1;
                log.error("用户{}存在被重复触达的风险！！",pushListInfo.getUserPhone());

                pushListInfo.setPushStatus("C");
                pushListInfo.setCallbackCode("-1");
                pushListInfo.setPushDate(new Date());
            }

            if(!repeatFlag)
            {
                //调用供应商短信接口进行推送
                message=new Message();
                message.setMobile(pushListInfo.getUserPhone());
                message.setContent(pushListInfo.getPushContent());
                try {
                    result=sendSms.singleSend(message);
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
                operations.set("PUSH_"+pushListInfo.getUserPhone(),pushListInfo.getUserPhone(),timeout);

            }
            userlist.add(pushListInfo);

        }

        //输出到推送日志通道

        if(userlist.size()>0)
        {
            pushListMapper.updateSendStatus(userlist);
        }

        //写入到触达日志中

        PushLog repeatLog = new PushLog();
        repeatLog.setLogType("0");
        repeatLog.setLogContent("重复推送" + repeatUserCount + "人");
        repeatLog.setUserCount((long) repeatUserCount);
        repeatLog.setLogDate(new Date());
        pushLogMapper.insertPushLog(repeatLog);
    }

    @Override
    public int push(String uid, String messageContent) {

        //发送类
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //短信接口
        Message  message=new Message();
        message.setMobile(uid);
        message.setContent(messageContent);
        int result=sendSms.singleSend(message);

        return result;
    }

    @Override
    public int batchPush(String messageContent, List<PushListLager> pushList) {
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //要进行推送的目标人群
        List<String> targetMobileList=Lists.newArrayList();

        //进行防骚扰验证
        List<String> mobileList=pushList.stream().map(p->p.getUserPhone()).collect(Collectors.toList());

        ValueOperations<String,String> operations=redisTemplate.opsForValue();
        //获取防骚扰拦截的时间
        int timeout=dailyProperties.getRepeatPushDays()*86400;

        String value="";
        int repeatUserCount=0;
        for(String mobile:mobileList)
        {
            //判断是否会存在骚扰拦截
            value=operations.get("PUSH_"+mobile);
            if(null!=value&&!"".equals(value))
            {
                repeatUserCount+=1;
                log.error("用户{}存在被重复触达的风险！！",mobile);
            }else
            {
                targetMobileList.add(mobile);
                //加入到redis中去
                operations.set("PUSH_"+mobile,mobile,timeout);
            }
        }

        //输出到日志通道
        Message message=new Message();
        message.setContent(messageContent);
        message.setMobile(targetMobileList.stream().collect(Collectors.joining(",")));

        //写入到触达日志中
        if(repeatUserCount != 0) {
            PushLog repeatLog = new PushLog();
            repeatLog.setLogType("0");
            repeatLog.setLogContent("重复推送" + repeatUserCount + "人");
            repeatLog.setUserCount((long) repeatUserCount);
            repeatLog.setLogDate(new Date());
            pushLogMapper.insertPushLog(repeatLog);
        }
        return sendSms.batchSend(message);
    }
}
