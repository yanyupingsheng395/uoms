package com.linksteady.operate.push.impl;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
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

    //短信
    private static final String PUSH_TYPE_SMS="SMS";
    //微信
    private static final String PUSH_TYPE_WX="WX";
    //测试 打印
    private static final String PUSH_TYPE_NONE="NONE";

    @Override
    public void push(List<PushListInfo> list) {
        log.info("当前选择的触达方式为{}，本批次触达人数:{}",dailyProperties.getPushType(),list.size());
        PushMessageService PushMessageService=getPushStrategy();
        PushMessageService.push(list);
    }

    @Override
    public int push(String uid, String messageContent) {
        PushMessageService PushMessageService=getPushStrategy();
        int result=PushMessageService.push(uid,messageContent);

        return result;
    }

    private PushMessageService getPushStrategy()
    {
        PushMessageService pushMessageService=null;
        if(PUSH_TYPE_SMS.equals(dailyProperties.getPushType()))
        {
            pushMessageService=pushSmsService;
        }else if(PUSH_TYPE_WX.equals(dailyProperties.getPushType()))
        {
            //微信消息
            pushMessageService=pushWxMessageService;
        }else if(PUSH_TYPE_NONE.equals(dailyProperties.getPushType()))
        {
            //测试，打印
            pushMessageService=pushDefaultService;
        }

        return pushMessageService;
    }
}
