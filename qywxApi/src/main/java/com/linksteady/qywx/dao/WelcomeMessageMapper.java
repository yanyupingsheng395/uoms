package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.domain.WelcomeMpParam;

import java.time.LocalDateTime;

public interface WelcomeMessageMapper {

    WelcomeMpParam getWelcomeMpParam();

    /**
     * 更新欢迎语小程序封面图片的midia_id信息
     */
    void updateWcMediaExpireInfo(String mediaId, LocalDateTime expreDt);

    /**
     * 获取当前使用的欢迎语
     */
    QywxWelcome getValidWelcome();
}
