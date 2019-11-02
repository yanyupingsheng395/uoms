package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListLager;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
public interface PushLargeListMapper {

    /**
     * 获取待推送的名单数
     * @return
     */
    int getPushLargeListCount(int currentHour);

    /**
     * 分页获取待推送的名单
     * @return
     */
    List<PushListLager> getPushLargeList(int currentHour, String sms, int start, int end);

    /**
     * 通过当前时间获取待推送的所有短信内容
     * @param currentHour
     * @return
     */
    List<String> getSmsContentList(int currentHour);

    /**
     * 更新推送状态 pushIdList为push_id的列表；status为状态
     */
    void updatePushState( List<Long> pushIdList,String status);

    /**
     *  通过当前时间和短信内容获取待推送的人数
     * @param currentHour
     * @param sms
     * @return
     */
    int getPushListCountBySms(int currentHour, String sms);

}
