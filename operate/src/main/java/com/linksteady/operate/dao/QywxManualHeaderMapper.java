package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxManualDetail;
import com.linksteady.operate.domain.QywxManualHeader;
import com.linksteady.operate.domain.QywxPushList;

import java.time.LocalDateTime;
import java.util.List;

public interface QywxManualHeaderMapper {

    int checkFollowerId(String followerId);

    int checkExternalUserId(String externalUserId,String followerId);

    void saveQywxManualHeader(QywxManualHeader qywxManualHeader);

    void saveQywxManualDetail(List<QywxManualDetail> list);

    int getHeaderListCount();

    List<QywxManualHeader> getHeaderListData(int limit, int offset);

    void deleteManualHeaderData(Long headId);

    void deleteManualDetail(Long headId);

    String getHeadStatus(Long headId);

    int updateStatusToPlaning(Long headId, String status, String initialStatus, LocalDateTime pushtime);

    QywxManualHeader getQywxManualHeader(Long headId);

    List<QywxManualDetail> getQywxManualDetail(Long headId);

    int getQywxManualDetailCount(Long headId, String followerUserId);

    List<QywxManualDetail> getQywxManualDetailByPage(Long headId, String followerUserId, int page, int size);

    void updatePushList(long pushId, String status, String msgId, String failList, String remark);

    void insertPushList(QywxPushList qywxPushList);

    void updatePushId(List<Long> detailIdList, long pushId, String msgId);

    List<String> getNotExistsFollowUser(long headId);

    List<QywxManualDetail> getNotExistsContact(long headId);

    int getPushDetailStatus(long headId);

    QywxManualHeader getManualPushById(Long headId, String status);

    void updateTotalNum(int totalNum, long headId);

    void delFolluser(List<String> notExistsFollowUserList,Long headId);

    void delContact(QywxManualDetail detail);

    void updateUserNumber(long headId);
}
