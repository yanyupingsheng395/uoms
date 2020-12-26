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
}
