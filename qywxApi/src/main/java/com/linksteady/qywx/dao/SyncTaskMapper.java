package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.domain.SyncTask;
import com.linksteady.qywx.vo.FollowUserVO;

import java.util.List;

public interface SyncTaskMapper {

    void saveFollowUser(String corpId,List<FollowUserVO> followUserList);

    void saveSyncTask(SyncTask syncTask);

    void updateSyncTask(String taskId,String status);

    void updateFollowUserFlag();

    void deleteFollowUser();

    void updateDeptDisabled();

    void saveDept(long id, long parentId, String deptName, int orderNo);

    void savePartyChangeFlag(String status);

    void saveAuthCodeChangeFlag(String status);

    void saveFollowUserChangeFlag(String status);

    void saveExternalContactList(List<ExternalContact> externalContactList);

    /**
     * 删除外部联系人
     */
    void deleteExternalContact(String followUserId,String externalUserId);

    /**
     * 新增或更新外部联系人
     */
    void saveExternalContact(ExternalContact externalContact);

    void updateExternalContactDeleteFlag();

    void deleteExternalContactDeleteFlag();

}
