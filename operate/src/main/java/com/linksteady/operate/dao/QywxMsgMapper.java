package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxMsg;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
public interface QywxMsgMapper {

    void saveData(QywxMsg qywxMsg);

    List<QywxMsg> getDataListPage(int limit, int offset);

    int getTotalCount();

    void updateQywxMsg(QywxMsg qywxMsg);

    void deleteDataById(String id);

    QywxMsg getDataById(String id);
}
