package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxChatBatchMsg;

import java.util.List;

public interface QywxChatBatchMsgService {
    int getCount();

    List<QywxChatBatchMsg> getDataList(int limit, int offset);

    void saveData(QywxChatBatchMsg qywxChatBatchMsg);

    void deleteById(long id);
}
