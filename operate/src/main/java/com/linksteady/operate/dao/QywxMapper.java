package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QyWxMsg;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
public interface QywxMapper {

    void saveData(QyWxMsg qyWxMsg);
    List<QyWxMsg> getDataListPage(int limit, int offset);
    int getTotalCount();
    void updateQyWxMsg(QyWxMsg qyWxMsg);
    void deleteDataById(String id);
}
