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

    void deleteById(long id);

    List<QywxWelcome> getDataById(long id);

    void updateData(QywxWelcome qywxWelcome);

    void updateStartStatus(long id, String status);

    void updateStopStatus(long id, String status);
}
