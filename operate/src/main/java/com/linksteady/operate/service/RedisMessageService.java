package com.linksteady.operate.service;

public interface RedisMessageService {

    /**
     * 接收推送的心跳信息
     */
    void receivePushHeartBeat(String pushHeartBeatMessage);

    /**
     * 即时发送短信
     */
    void  sendPhoneMessage(String phoneNum,String smsContent);
}
