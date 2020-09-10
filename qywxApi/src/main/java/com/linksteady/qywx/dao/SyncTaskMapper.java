package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.domain.SyncTask;

import java.util.List;

public interface SyncTaskMapper {

    void saveFollowUser(String corpId,List<String> followUserList);

    void saveSyncTask(List<SyncTask> taskList);

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

}
