package com.linksteady.operate.push.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
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

    @Override
    public void push(List<PushListInfo> list) {
        //发送类
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //短信接口
        Message message=null;
        //触达结果
        int result=-1;

        //用户的列表
        List<PushListInfo> userlist= Lists.newArrayList();

        ValueOperations<String,String> operations=redisTemplate.opsForValue();
        //获取防骚扰拦截的时间
        int timeout=dailyProperties.getRepeatPushDays()*86400;

        boolean repeatFlag=false;

        for(PushListInfo pushListInfo:list)
        {
            repeatFlag=false;
            ////开启了防骚扰拦截
            if("Y".equals(dailyProperties.getRepeatPush()))
            {
                String value=operations.get("PUSH_"+pushListInfo.getUserPhone());
                if(null!=value&&!"".equals(value))
                {
                    repeatFlag=true;
                    log.error("用户{}存在被重复触达的风险！！",pushListInfo.getUserPhone());

                    pushListInfo.setPushStatus("C");
                    pushListInfo.setCallbackCode("-1");
                    pushListInfo.setPushDate(new Date());
                }
            }

            if(!repeatFlag)
            {
                //调用供应商短信接口进行推送
                message=new Message();
                message.setMobile(pushListInfo.getUserPhone());
                message.setContent(pushListInfo.getPushContent());
                result=sendSms.singleSend(message);
                log.info("模拟推送:{}-{}",pushListInfo.getUserPhone(),pushListInfo.getPushContent());

                if(result==0)
                {
                    pushListInfo.setPushStatus("S");
                }else
                {
                    pushListInfo.setPushStatus("F");
                }
                pushListInfo.setCallbackCode(String.valueOf(result));
                pushListInfo.setPushDate(new Date());

                //如何开启了放骚扰，则需要将推送结果存入redis
                if("Y".equals(dailyProperties.getRepeatPush()))
                {
                    //已发送用户存入redis
                    operations.set("PUSH_"+pushListInfo.getUserPhone(),pushListInfo.getUserPhone(),timeout);
                }

            }
            userlist.add(pushListInfo);

        }
        if(userlist.size()>0)
        {
            pushListMapper.updateSendStatus(userlist);
        }
    }

    @Override
    public int push(String uid, String messageContent) {
        int result=0;
        //发送类
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        //短信接口
        Message  message=new Message();
        message.setMobile(uid);
        message.setContent(messageContent);
        result=sendSms.singleSend(message);

        if(result!=0)
        {
            result=-1;
        }

        return result;
    }
}
