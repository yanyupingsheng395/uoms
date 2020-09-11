package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxWelcome;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/3
 */
public interface QywxWelcomeService {

    Integer saveData(QywxWelcome qywxWelcome);

    int getDataCount();

    List<QywxWelcome> getDataList(Integer limit, Integer offset);

    void deleteById(String id);

    QywxWelcome getDataById(String id);

    void updateData(QywxWelcome qywxWelcome);

    void updateStatus(String id, String status);
}
