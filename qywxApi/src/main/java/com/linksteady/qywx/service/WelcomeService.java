package com.linksteady.qywx.service;

import com.linksteady.common.domain.QywxMessage;

public interface WelcomeService {

    /**
     * 获取当前配置的有效的欢迎语信息
     */
    QywxMessage getValidWelcomeMessage();


    void sendWelcomeMessage(String welcomeCode,String exernalUserId)  throws Exception;

}
