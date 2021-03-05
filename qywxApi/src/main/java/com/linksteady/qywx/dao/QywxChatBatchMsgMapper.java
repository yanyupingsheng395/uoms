package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxChatBatchMsg;

import java.util.List;

public interface QywxChatBatchMsgMapper {
    int getCount();

    List<QywxChatBatchMsg> getDataList(int limit, int offset);

    void saveData(QywxChatBatchMsg qywxChatBatchMsg);

    void deleteById(long id);

    QywxChatBatchMsg getChatBatchmsg(long batchMsgId);
}
