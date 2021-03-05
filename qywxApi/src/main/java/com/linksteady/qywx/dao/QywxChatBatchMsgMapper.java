package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxChatBatchMsg;

import java.util.List;

public interface QywxChatBatchMsgMapper {
    int getCount();

    List<QywxChatBatchMsg> getDataList(int limit, int offset);

    void saveData(QywxChatBatchMsg qywxChatBatchMsg);

    void deleteById(long id);

    QywxChatBatchMsg getChatBatchmsg(long batchMsgId);

    /**
     * 通过群主，获取所有群的外部成员
     * @param owner
     * @return
     */
    List<String> getUserID(String owner);

    void upadteStatus(long batchMsgId, String username, String status);
}
