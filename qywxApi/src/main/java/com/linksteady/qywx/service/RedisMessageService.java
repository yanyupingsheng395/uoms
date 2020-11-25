package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.HeartBeatInfo;

public interface RedisMessageService {

    /**
     * 接收推送的心跳信息
     */
    void receivePushHeartBeat(String pushHeartBeatMessage);

    /**
     * 即时发送短信
     */
    void  sendPhoneMessage(String phoneNum, String smsContent);

    /**
     * 发送 启动/停止/刷新配置 通知
     */
    void sendPushSingal(HeartBeatInfo heartBeatInfo);
}
