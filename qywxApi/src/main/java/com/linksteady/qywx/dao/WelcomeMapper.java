package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxWelcome;

public interface WelcomeMapper {

    /**
     * 获取当前使用的欢迎语
     */
    QywxWelcome getValidWelcome();
}
