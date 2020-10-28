package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxWelcome;

public interface WelcomeMessageMapper {

    /**
     * 获取当前使用的欢迎语
     */
    QywxWelcome getValidWelcome();
}
