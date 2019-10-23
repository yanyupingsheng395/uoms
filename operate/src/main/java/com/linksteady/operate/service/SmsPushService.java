package com.linksteady.operate.service;

import com.linksteady.operate.domain.PushListLager;

import java.util.List;

/**
 * 短信发送接口
 * @author hxcao
 * @date 2019-10-23
 */
public interface SmsPushService {
    /**
     * 获取待推送的名单数量
     * @param currentHour
      */
    int getPushListCount(int currentHour);

    /**
     * 获取待推送的名单
     * @param currentHour
     * @return
     */
    List<PushListLager> getPushList(int currentHour, String sms, int start, int end);

    /**
     * 获取所有不同的短信
     * @param currentHour
     * @return
     */
    List<String> getSmsContent(int currentHour);

    /**
     * 更新数据状态
     * @param smsContent
     */
    void updatePushState(List<String> smsContent, Long maxPushId);

    int getPushListCountBySms(int currentHour, String sms);

    Long getMaxPushId(int currentHour);
}
