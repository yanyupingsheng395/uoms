package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ManualHeader;
import com.linksteady.operate.domain.QywxManualDetail;
import com.linksteady.operate.domain.QywxManualHeader;
import com.linksteady.operate.domain.QywxPushList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019/12/25
 */
public interface QywxManualHeaderMapper {

    int checkFollowerId(String followerId);

    int checkExternalUserId(String externalUserId);

    void saveQywxManualHeader(QywxManualHeader qywxManualHeader);

    void saveQywxManualDetail(List<QywxManualDetail> list);

    int getHeaderListCount();

    List<QywxManualHeader> getHeaderListData(int limit, int offset);

    void deleteManualHeaderData(Long headId);

    void deleteManualDetail(Long headId);

    String getHeadStatus(Long headId);

    int updateStatusToPlaning(Long headId,String status,String initialStatus);

    QywxManualHeader getQywxManualHeader(Long headId);

    List<QywxManualDetail> getQywxManualDetail(Long headId);

    int getQywxManualDetailCount(Long headId, String followerUserId);

    List<QywxManualDetail> getQywxManualDetailByPage(Long headId, String followerUserId, int page, int size);

    void updatePushList(long pushId, String status, String msgId, String failList, String remark);

    void insertPushList(QywxPushList qywxPushList);

    void updatePushId(List<Long> detailIdList, long pushId, String msgId);
}
