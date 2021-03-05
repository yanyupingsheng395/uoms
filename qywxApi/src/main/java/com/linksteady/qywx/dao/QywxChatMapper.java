package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QywxChatMapper {

    int getCount(String owner,String status);

    List<QywxChatBase> getDataList(int limit, int offset,String owner,String status);

    int getCustomerListCount(String chatId);

    List<QywxChatDetail> getCustomerList(int limit, int offset, String chatId);

    void insertDetail(@Param("chatDetailList") List<QywxChatDetail> list);

    void insertChatBase(QywxChatBase qywxChatBase);

    void deleteChatBase(String chatId);

    void deleteChatDetail(String chatId);

    QywxChatBase getChatBaseDetail(String chatId);

    List<QywxChatStatistics> getDetailData(String chatId);

    void insertChatDay();

    List<QywxChatStatisticsVO> getChatSummary(String chatId);

    List <QywxChatStatistics>  getChatStatisticsById(String chatId);

    List<QywxChatStatistics> getChatStatistics();

    void updateNumber(@Param("numlist") List<QywxChatStatistics> numlist);

    List<QywxChatBase>  getChatBaseData();

    FriendsNumVO getFriendsNum(String chatId);
}
