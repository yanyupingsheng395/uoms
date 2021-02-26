package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxChatDetail;
import com.linksteady.qywx.domain.QywxChatBase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerBaseMapper {
    int getCount(String owner,String status);

    List<QywxChatBase> getDataList(int limit, int offset,String owner,String status);

    int getCustomerListCount(String chatId);

    List<QywxChatDetail> getCustomerList(int limit, int offset, String chatId);

    void insertDetail(@Param("chatDetailList") List<QywxChatDetail> list);
    void insertChatBase(@Param("chatBaseList") List<QywxChatBase> list);

    void deleteChatBase(String chatId);

    void deleteChatDetail(String chatId);

    QywxChatBase getChatBaseDetail(String chatId);
}
