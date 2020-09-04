package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.ExternalContact;
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

    /**
     * 设置所有的部门状态为不可用
     */
    void updateDeptDisabled();

    /**
     * 更新部门信息
     */
    void saveDept(long id,long parentId,String name,int orderNo);

    /**
     * 更新企业微信端更新标志
     */
    void saveChangeFlag(String changeCode);

    /**
     * 保存外部联系人信息
     */
    void saveExternalContactList(List<ExternalContact> externalContactList);
}
