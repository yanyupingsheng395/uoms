package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.SyncTask;

import java.util.List;

public interface SyncTaskService {

    /**
     * 保存导购信息
     */
    void saveFollowUser(String corpId,List<String> followUserList);

    /**
     * 新增同步任务
     */
     void saveSyncTask(List<SyncTask> taskList);

    /**
     * 更新同步任务
     */
    void updateSyncTask(String taskId,String status);
}
