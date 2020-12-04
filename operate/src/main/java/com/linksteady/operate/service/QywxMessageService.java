package com.linksteady.operate.service;

import com.linksteady.common.domain.QywxMessage;

import java.util.List;

public interface QywxMessageService {

    /**
     * 发送企业微信消息(一对一推送)
     * @param message
     * @param sender
     * @param externalUserList
     */
    String pushQywxMessage(QywxMessage message, String sender, List<String> externalUserList);

    /**
     * 获取corpID
     */
    String getCorpId();

    /**
     * 获取mpAppid
     */
    String getMpAppId();

}
