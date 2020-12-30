package com.linksteady.qywx.service;

import com.linksteady.common.domain.QywxMessage;
import com.linksteady.qywx.domain.QywxWelcome;

import java.util.List;

public interface WelcomeService {

    /**
     * 获取当前配置的有效的欢迎语信息
     */
    QywxMessage getValidWelcomeMessage() throws Exception;


    void sendWelcomeMessage(String welcomeCode,String exernalUserId)  throws Exception;

    Integer saveData(QywxWelcome qywxWelcome);

    int getDataCount();

    List<QywxWelcome> getDataList(Integer limit, Integer offset);

    void deleteById(String id);

    QywxWelcome getDataById(String id);

    void updateData(QywxWelcome qywxWelcome);

    void updateStatus(String id, String status);

}
