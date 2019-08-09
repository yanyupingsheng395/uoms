package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyPushInfo;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyPushService {

    void generatePushList(String headerId);

    /**
     * 获取当前待发送的短信列表
     */
    List<DailyPushInfo> getSendSmsList();

    /**
     * 保存文案信息
     * @param targetList
     */
    void updatePushContent( List<DailyPushInfo> targetList);

    /**
     * 更新短信发送的状态
     *
     */
    void updateSendStatus(List<DailyPushInfo> list,String status);

    /**
     * 更新主记录为 完成推送，效果统计中
     */
    void updateHeaderToDone();

    /**
     * 更新主记录的触达统计信息
     */
    void updateHeaderSendStatis();

    /**
     * 发送消息
     * @return
     */
    int sendMessage(String userIdentify,String smsContent);

    /**
     * 更新统计表中的触达信息
     */
    void updatePushStatInfo();

}
