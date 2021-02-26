package com.linksteady.qywx.service;

import com.linksteady.common.domain.QywxMessage;
import com.linksteady.qywx.domain.QywxWelcome;

import java.util.List;

public interface WelcomeService {

    /**
     * 获取当前配置的有效的欢迎语信息
     */
    QywxMessage getValidWelcomeMessage() throws Exception;

    /**
     * 进行欢迎语发发送
     * @param welcomeCode
     * @param exernalUserId
     * @throws Exception
     */
    void sendWelcomeMessage(String welcomeCode,String exernalUserId)  throws Exception;

    Long saveData(QywxWelcome qywxWelcome);

    int getDataCount();

    List<QywxWelcome> getDataList(Integer limit, Integer offset);

    void deleteById(long id);

    QywxWelcome getDataById(long id);

    void updateData(QywxWelcome qywxWelcome);

    void updateStatus(long id, String status);

    /**
     * 获取欢迎语对应小程序卡片的图片内容
     */
    byte[] getWelcomeMpMediaContent(long id);

}
