package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxChatDetail;
import com.linksteady.qywx.domain.QywxChatBase;
import com.linksteady.qywx.domain.QywxChatStatistics;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

public interface QywxChatService {

    int getCount(String owner,String status);

    /**
     * 获取客户群列表
     * @param limit
     * @param offset
     * @return
     */
    List<QywxChatBase> getDataList(int limit, int offset,String owner,String status);

    /**
     * 获取客户群人数
     * @param
     * @return
     */
    int getCustomerListCount(String chatId);

    List<QywxChatDetail> getCustomerList(int limit, int offset, String chatId);

    /**
     * 获取客户群列表，存入数据库
     * @throws WxErrorException
     */
    void syncQywxChatList() throws WxErrorException;

    /**
     * 客户群解散，删除数据库中uo_qywx_chat_detail 和uo_qywx_chat_base数据
     * @param chatId
     */
    void delChatBase(String chatId);

    /**
     * 客户群变更
     * @param chatId
     */
    void updateChat(String chatId) throws WxErrorException;

    /**
     * 根据客户群ID获取客户群详细信息
     * @param chatId
     * @return
     */
    QywxChatBase getChatBaseDetail(String chatId);

    /**
     * 获取群聊七天内的人员变化
     * @param chatId
     * @return
     */
    List<QywxChatStatistics> getDetailData(String chatId);

    /**
     * 同步群人数统计
     */
    void syncChatStatistics();
}
