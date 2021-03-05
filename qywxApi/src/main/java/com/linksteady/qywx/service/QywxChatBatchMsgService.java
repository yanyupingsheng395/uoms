package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxChatBatchMsg;

import java.util.List;

public interface QywxChatBatchMsgService {
    /**
     * 获取群发消息数量
     * @return
     */
    int getCount();

    /**
     * 分页获取群发消息列表
     * @param limit
     * @param offset
     * @return
     */
    List<QywxChatBatchMsg> getDataList(int limit, int offset);

    /**
     * 新增群发消息
     * @param qywxChatBatchMsg
     */
    void saveData(QywxChatBatchMsg qywxChatBatchMsg);

    /**
     * 删除群发消息
     * @param id
     */
    void deleteById(long id);

    /**
     * 推送群消息
     * @param batchMsgId
     * @return
     */
    String pushMessage(long batchMsgId) throws Exception;
}
