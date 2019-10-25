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
     * 分页获取待推送的名单
     * @return
     */
    List<PushListLager> getPushList(int currentHour, String sms, int start, int end);

    /**
     * 通过当前时间获取待推送的所有短信内容
     * @param currentHour
     * @return
     */
    List<String> getSmsContent(int currentHour);

    /**
     * 通过最大推送id和短信内容更新数据状态
     */
    void updatePushState(@Param("smsContent") List<String> smsContent, @Param("maxPushId") Long maxPushId);

    /**
     *  通过当前时间和短信内容获取待推送的人数
     * @param currentHour
     * @param sms
     * @return
     */
    int getPushListCountBySms(int currentHour, String sms);

    /**
     * 获取最大的推送ID，根据推送ID条件，后期更新数据状态，以免发生数据漏发
     * @param currentHour
     * @return
     */
    Long getMaxPushId(int currentHour);
}
