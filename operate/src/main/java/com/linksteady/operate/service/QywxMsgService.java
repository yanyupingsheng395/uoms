package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxMsg;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
public interface QywxMsgService {

    void saveData(QywxMsg qyWxMsg);

    List<QywxMsg> getDataListPage(int limit, int offset);

    int getTotalCount();

    void updateQyWxMsg(QywxMsg qyWxMsg);

    void deleteDataById(String id);

    QywxMsg getDataById(String id);

    void refreshDataById(String id);
}
