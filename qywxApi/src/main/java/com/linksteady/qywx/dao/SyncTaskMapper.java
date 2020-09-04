package com.linksteady.qywx.dao;

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

    void savePartyChangeFlag();

    void saveAuthCodeChangeFlag();

    void saveFollowUserChangeFlag();
}
