package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxWelcome;

import java.util.List;

public interface WelcomeMapper {

    /**
     * 获取当前使用的欢迎语
     */
    QywxWelcome getValidWelcome();

    void saveData(QywxWelcome qywxWelcome);

    int getDataCount();

    List<QywxWelcome> getDataList(Integer limit, Integer offset);

    void deleteById(String id);

    List<QywxWelcome> getDataById(String id);

    void updateData(QywxWelcome qywxWelcome);

    void updateStartStatus(String id, String status);

    void updateStopStatus(String id, String status);
}
