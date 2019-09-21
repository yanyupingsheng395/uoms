package com.linksteady.operate.push.impl;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.push.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class PushMessageServiceImpl implements PushMessageService {

    @Autowired
    DailyProperties dailyProperties;

    @Autowired
    PushDefaultServiceImpl pushDefaultService;

    @Autowired
    PushSmsServiceImpl pushSmsService;

    @Autowired
    PushWxMessageServiceImpl pushWxMessageService;

    @Override
    public void push(List<DailyPushInfo> list) {
        log.info("当前选择的触达方式为{}，本批次触达人数:{}",dailyProperties.getPushType(),list.size());
        if("SMS".equals(dailyProperties.getPushType()))
        {
            //短信触达
            pushSmsService.push(list);
        }else if("WX".equals(dailyProperties.getPushType()))
        {
            //微信消息
            pushWxMessageService.push(list);
        }else if("NONE".equals(dailyProperties.getPushType()))
        {
            //测试，打印
            pushDefaultService.push(list);
        }
    }

    @Override
    public int push(String uid, String messageContent) {
        int result=0;
        if("SMS".equals(dailyProperties.getPushType()))
        {
            //短信触达
            result=pushSmsService.push(uid,messageContent);
        }else if("WX".equals(dailyProperties.getPushType()))
        {
            //微信消息
            result=pushWxMessageService.push(uid,messageContent);
        }else if("NONE".equals(dailyProperties.getPushType()))
        {
            //测试，打印
            result=pushDefaultService.push(uid,messageContent);
        }
        return result;
    }
}
