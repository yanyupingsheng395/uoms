package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxChatDetail;
import com.linksteady.qywx.domain.QywxChatBase;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

public interface CustomerBaseService {

    int getCount();

    /**
     * 获取客户群列表
     * @param limit
     * @param offset
     * @return
     */
    List<QywxChatBase> getDataList(int limit, int offset);

    /**
     * 获取客户群人数
     * @param
     * @return
     */
    int getCustomerListCount(String chatId);

    List<QywxChatDetail> getCustomerList(int limit, int offset, String chatId);

    List<FollowUser> getFollowUser();

    /**
     * 获取客户群列表，存入数据库
     * @param cursor   下一次查询的游标，第一次查询不用
     * @throws WxErrorException
     */
    void getQywxChatList( String cursor) throws WxErrorException;

    /**
     * 新建客户群，同步数据
     * @param chatId   客户群ID
     * @param flag      是否需要单独存主表
     */
    QywxChatBase saveChatBase(String chatId,boolean flag) throws WxErrorException;

    /**
     * 客户群解散，删除数据库中uo_qywx_chat_detail 和uo_qywx_chat_base数据
     * @param chatId
     */
    void deleChatBase(String chatId);

    /**
     * 客户群变更
     * @param chatId
     */
    void updateChat(String chatId) throws WxErrorException;
}
