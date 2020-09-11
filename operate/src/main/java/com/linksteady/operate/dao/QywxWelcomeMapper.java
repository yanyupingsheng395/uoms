package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxWelcome;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/3
 */
public interface QywxWelcomeMapper {

    void saveData(QywxWelcome qywxWelcome);

    int getDataCount();

    List<QywxWelcome> getDataList(Integer limit, Integer offset);

    void deleteById(String id);

    List<QywxWelcome> getDataById(String id);

    void updateData(QywxWelcome qywxWelcome);

    void updateStartStatus(String id, String status);

    void updateStopStatus(String id, String status);
}
