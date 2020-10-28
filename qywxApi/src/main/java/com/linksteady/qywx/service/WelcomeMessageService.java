package com.linksteady.qywx.service;

import com.linksteady.common.domain.QywxMessage;

public interface WelcomeMessageService {

    /**
     * 获取当前配置的有效的欢迎语信息
     */
    QywxMessage getValidWelcomeMessage();

}
