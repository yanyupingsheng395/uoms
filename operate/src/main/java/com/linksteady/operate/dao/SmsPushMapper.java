package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListLager;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
public interface SmsPushMapper {
    /**
     * 获取待推送的名单数
     * @return
     */
    int getPushListCount(int currentHour);

    /**
     * 获取待推送的名单
     * @return
     */
    List<PushListLager> getPushList(int currentHour, String sms, int start, int end);

    /**
     * 获取短信内容
     * @param currentHour
     * @return
     */
    List<String> getSmsContent(int currentHour);

    /**
     * 更新数据状态
     */
    void updatePushState(@Param("smsContent") List<String> smsContent, @Param("maxPushId") Long maxPushId);

    int getPushListCountBySms(int currentHour, String sms);

    Long getMaxPushId(int currentHour);
}
