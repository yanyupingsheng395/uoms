package com.linksteady.operate.service;

import com.linksteady.operate.domain.PushListLarge;

import java.util.List;

/**
 * 短信发送接口
 * @author hxcao
 * @date 2019-10-23
 */
public interface PushLargeListService {
    /**
     * 获取待推送的名单数量
     * @param currentHour
      */
    int getPushLargeListCount(int currentHour);

    /**
     * 获取待推送的名单
     * @param currentHour
     * @return
     */
    List<PushListLarge> getPushLargeList(int currentHour, String sms, int start, int end);

    /**
     * 获取所有不同的短信
     * @param currentHour
     * @return
     */
    List<String> getSmsContentList(int currentHour);


    int getPushListCountBySms(int currentHour, String sms);

}
